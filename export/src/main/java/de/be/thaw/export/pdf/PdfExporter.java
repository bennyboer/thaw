package de.be.thaw.export.pdf;

import de.be.thaw.core.document.Document;
import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.knuthplass.KnuthPlassTypeSetter;
import de.be.thaw.typeset.knuthplass.config.LineBreakingConfig;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWord;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWordPart;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.page.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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

            PDFont pdfFont = PDType1Font.HELVETICA;
            float fontSize = 16;
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
            TypeSetter typeSetter = createTypeSetter(pdfFont);

            List<Page> pages = typeSetter.typeset(document);

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            ExportContext ctx = new ExportContext(contentStream, pdfFont, fontSize, width, height, margin, margin, margin, margin, startY, startX, endY, endX, leading);

            contentStream.beginText();
            contentStream.setFont(pdfFont, fontSize);
            contentStream.newLineAtOffset(startX, startY);

            // TODO

            contentStream.endText();
            contentStream.close();

            doc.save(path.toFile());
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    private TypeSetter createTypeSetter(PDFont pdfFont) {
        return new KnuthPlassTypeSetter(LineBreakingConfig.newBuilder()
                .setFontDetailsSupplier(new FontDetailsSupplier() {
                    @Override
                    public double getCodeWidth(Node node, int code) {
                        try {
                            return pdfFont.getWidth(code);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }

                    @Override
                    public double getStringWidth(Node node, String str) {
                        try {
                            return pdfFont.getStringWidth(str);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }

                    @Override
                    public double getSpaceWidth(Node node) {
                        return pdfFont.getSpaceWidth();
                    }
                })
                .setGlueConfig(new GlueConfig() {
                    @Override
                    public double getInterWordStretchability(char lastChar) {
                        try {
                            return pdfFont.getWidth(' ') * 0.5;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }

                    @Override
                    public double getInterWordShrinkability(char lastChar) {
                        try {
                            return pdfFont.getWidth(' ') * 0.33;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
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
