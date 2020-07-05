package de.be.thaw.export.pdf;

import de.be.thaw.font.util.FontManager;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.FontVariantLocator;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.typeset.util.Size;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Context used during PDF export.
 */
public class ExportContext {

    /**
     * Cache for already loaded and embedded fonts.
     */
    private final Map<FontVariantLocator, PDFont> fontCache = new HashMap<>();

    /**
     * The document to export to.
     */
    private final PDDocument document;

    /**
     * Size of the pages.
     */
    private Size pageSize;

    /**
     * Insets of the pages.
     */
    private Insets pageInsets;

    /**
     * The current PDF page to export.
     */
    private PDPage currentPage;

    /**
     * Current PDF content stream.
     */
    private PDPageContentStream contentStream;

    public ExportContext(PDDocument document) {
        this.document = document;
    }

    /**
     * Get the PDF document to export to.
     *
     * @return document
     */
    public PDDocument getDocument() {
        return document;
    }

    public Size getPageSize() {
        return pageSize;
    }

    public void setPageSize(Size pageSize) {
        this.pageSize = pageSize;
    }

    public Insets getPageInsets() {
        return pageInsets;
    }

    public void setPageInsets(Insets pageInsets) {
        this.pageInsets = pageInsets;
    }

    public PDPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(PDPage currentPage) {
        this.currentPage = currentPage;
    }

    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    public void setContentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
    }

    /**
     * Get the font for the passed node.
     *
     * @param node to get font for
     * @return font
     */
    public PDFont getFontForNode(Node node) {
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
                    font = PDType0Font.load(getDocument(), collection.getFontByName(locator.getFontName()), false);
                } else {
                    font = PDType0Font.load(getDocument(), new FileInputStream(new File(locator.getFontFile().getLocation())), false);
                }

                fontCache.put(locator, font);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return font;
    }

    /**
     * Get the font size for the passed node.
     *
     * @param node to get font size for
     * @return font size
     */
    public double getFontSizeForNode(Node node) {
        return 12.0; // TODO Get font size from document model properly when having a style model
    }

}
