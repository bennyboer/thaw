package de.be.thaw.export.pdf;

import de.be.thaw.core.document.Document;
import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.font.util.FontManager;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.FontVariantLocator;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.FormattedNode;
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
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exporter exporting documents to PDF.
 */
public class PdfExporter implements Exporter {

    /**
     * Cache for already loaded and embedded fonts.
     */
    private final Map<FontVariantLocator, PDFont> fontCache = new HashMap<>();

    /**
     * PDF document currently exporting to.
     */
    private PDDocument doc;

    @Override
    public void export(Document document, Path path) throws ExportException {
        fontCache.clear();

        try (PDDocument doc = new PDDocument()) {
            this.doc = doc;

            PDPage page = new PDPage();
            doc.addPage(page);

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
                TypeSetter typeSetter = createTypeSetter(fontSize, new Size(width, height), leading, quality);

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

            int pageCounter = 0;
            for (Page p : pages) {
                for (Element element : p.getElements()) {
                    if (element instanceof TextElement) {
                        TextElement te = (TextElement) element;

                        contentStream.beginText();
                        contentStream.setFont(getFontForNode(((TextElement) element).getNode()), fontSize); // TODO How to apply font per node?

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
     * Get the correct font for the passed node.
     *
     * @param node to get font for
     * @return font
     */
    private PDFont getFontForNode(Node node) {
        // TODO Get the font family properly using the thaw document model that has that setting set in the style model
        String familyName = "Cambria"; // Default font family for testing

        FontVariant variant = FontVariant.PLAIN;
        if (node instanceof FormattedNode) {
            Set<TextEmphasis> emphases = ((FormattedNode) node).getEmphases();

            boolean isBold = emphases.contains(TextEmphasis.BOLD);
            boolean isItalic = emphases.contains(TextEmphasis.ITALIC);

            if (isBold && isItalic) {
                variant = FontVariant.BOLD_ITALIC;
            } else if (isBold) {
                variant = FontVariant.BOLD;
            } else if (isItalic) {
                variant = FontVariant.ITALIC;
            }
        }

        FontVariantLocator locator = FontManager.getInstance().getFamily(familyName).orElseThrow().getVariantFont(variant).orElseThrow();

        PDFont font = fontCache.get(locator);
        if (font == null) {
            try {
                if (locator.getFontFile().isCollection()) {
                    TrueTypeCollection collection = new TrueTypeCollection(new File(locator.getFontFile().getLocation()));
                    font = PDType0Font.load(doc, collection.getFontByName(locator.getFontName()), false);
                } else {
                    font = PDType0Font.load(doc, new FileInputStream(new File(locator.getFontFile().getLocation())), false);
                }

                fontCache.put(locator, font);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return font;
    }

    /**
     * Create a type setter.
     *
     * @param fontSize   font size to use
     * @param docSize    size of the pages
     * @param lineHeight height of a line
     * @param quality    the quality (0 is the best, higher get worse)
     * @return type setter to use
     */
    private TypeSetter createTypeSetter(float fontSize, Size docSize, double lineHeight, int quality) {
        return new KnuthPlassTypeSetter(LineBreakingConfig.newBuilder()
                .setPageSize(docSize)
                .setLineHeight(lineHeight)
                .setLooseness(1 + quality)
                .setFirstLineIndent(20)
                .setFontDetailsSupplier(new FontDetailsSupplier() {
                    @Override
                    public double getCodeWidth(Node node, int code) {
                        try {
                            return getFontForNode(node).getWidth(code) / 1000 * fontSize;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }

                    @Override
                    public double getStringWidth(Node node, String str) {
                        try {
                            return getFontForNode(node).getStringWidth(str) / 1000 * fontSize;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return 0;
                    }

                    @Override
                    public double getSpaceWidth(Node node) {
                        return getFontForNode(node).getSpaceWidth() / 1000 * fontSize;
                    }
                })
                .setGlueConfig(new GlueConfig() {
                    @Override
                    public double getInterWordStretchability(Node node, char lastChar) {
                        return (getFontForNode(node).getSpaceWidth() / 1000 * fontSize / 2) * (quality + 1);
                    }

                    @Override
                    public double getInterWordShrinkability(Node node, char lastChar) {
                        return getFontForNode(node).getSpaceWidth() / 1000 * fontSize / 3;
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
