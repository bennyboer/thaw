package de.be.thaw.export.pdf;

import de.be.thaw.core.document.Document;
import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.font.util.FontFamily;
import de.be.thaw.font.util.FontManager;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.KnuthPlassTypeSetter;
import de.be.thaw.typeset.knuthplass.config.LineBreakingConfig;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWord;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWordPart;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.util.Size;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Exporter exporting documents to PDF.
 */
public class PdfExporter implements Exporter {

    @Override
    public void export(Document document, Path path) throws ExportException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            FontFamily family = FontManager.getInstance().getFamily("Calibri").orElseThrow();
            PDFont pdfFont = PDType0Font.load(doc, new FileInputStream(family.getVariantFont(FontVariant.PLAIN).orElseThrow().getLocation()), true);
            float fontSize = 12;
            float leading = 1.5f * fontSize;

            PDRectangle box = page.getMediaBox();
            float margin = 72;
            float width = box.getWidth() - 2 * margin;
            float height = box.getHeight() - 2 * margin;
            float startX = box.getLowerLeftX() + margin;
            float startY = box.getUpperRightY() - margin;
            float endX = box.getUpperRightX() - margin;
            float endY = box.getLowerLeftY() + margin;

            // Type set the document
            int quality = 0;
            List<Page> pages;
            while (true) {
                TypeSetter typeSetter = createTypeSetter(pdfFont, fontSize, new Size(width, height), leading, quality);

                try {
                    pages = typeSetter.typeset(document);
                    break;
                } catch (TypeSettingException e) {
                    System.out.println(e.getMessage());
                    System.out.println(">>> Will decrease type setting quality in order to succeed eventually");
                    quality++;
                }
            }

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            ExportContext ctx = new ExportContext(contentStream, pdfFont, fontSize, width, height, margin, margin, margin, margin, startY, startX, endY, endX, leading);

            int pageCounter = 0;
            for (Page p : pages) {
                for (Element element : p.getElements()) {
                    if (element instanceof TextElement) {
                        TextElement te = (TextElement) element;

                        contentStream.beginText();
                        contentStream.setFont(pdfFont, fontSize); // TODO How to apply font per node?

                        contentStream.newLineAtOffset((float) te.getPosition().getX() + margin, startY - (float) te.getPosition().getY());

                        contentStream.showText(te.getText());

                        contentStream.endText();
                    } else {
                        throw new ExportException(String.format(
                                "Cannot export element of type '%s'",
                                element.getClass().getSimpleName()
                        ));
                    }
                }

                contentStream.close();

                if (pageCounter < pages.size() - 1) {
                    page = new PDPage();
                    doc.addPage(page);
                    contentStream = new PDPageContentStream(doc, page);
                }

                pageCounter++;
            }

            doc.save(path.toFile());
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    /**
     * Create a type setter.
     *
     * @param pdfFont    font to use
     * @param fontSize   font size to use
     * @param docSize    size of the pages
     * @param lineHeight height of a line
     * @param quality    the quality (0 is the best, higher get worse)
     * @return type setter to use
     */
    private TypeSetter createTypeSetter(PDFont pdfFont, float fontSize, Size docSize, double lineHeight, int quality) {
        return new KnuthPlassTypeSetter(LineBreakingConfig.newBuilder()
                .setPageSize(docSize)
                .setLineHeight(lineHeight)
                .setLooseness(1 + quality)
                .setFirstLineIndent(20)
                .setFontDetailsSupplier(new FontDetailsSupplier() {
                    @Override
                    public double getCodeWidth(Node node, int code) {
                        try {
                            return pdfFont.getWidth(code) / 1000 * fontSize;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }

                    @Override
                    public double getStringWidth(Node node, String str) {
                        try {
                            return pdfFont.getStringWidth(str) / 1000 * fontSize;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }

                    @Override
                    public double getSpaceWidth(Node node) {
                        return pdfFont.getSpaceWidth() / 1000 * fontSize;
                    }
                })
                .setGlueConfig(new GlueConfig() {
                    @Override
                    public double getInterWordStretchability(char lastChar) {
                        return pdfFont.getSpaceWidth() / 1000 * fontSize / 2 * quality;
                    }

                    @Override
                    public double getInterWordShrinkability(char lastChar) {
                        return pdfFont.getSpaceWidth() / 1000 * fontSize / 3;
                    }
                })
                .setHyphenator(new Hyphenator() {
                    @Override
                    public HyphenatedWord hyphenate(String word) {
                        // TODO Implement actual hyphenation
                        return new HyphenatedWord(Collections.singletonList(new HyphenatedWordPart(word)));
                    }

                    @Override
                    public double getExplicitHyphenPenalty() {
                        return HyphenatedWordPart.DEFAULT_PENALTY;
                    }
                })
                .build());
    }

}
