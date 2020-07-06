package de.be.thaw.export.pdf;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.font.util.FontFamily;
import de.be.thaw.font.util.FontManager;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.FontVariantLocator;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.typeset.page.Page;
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

    /**
     * The current source page.
     */
    private Page currentSourcePage;

    /**
     * Current index of the source element to export.
     */
    private int currentSourceElementIndex;

    /**
     * Whether the elements are currently underlined.
     */
    private boolean currentlyUnderlined = false;

    /**
     * Descent of the underline.
     * Set when currentlyUnderlined = true.
     */
    private double underlineDescent;

    /**
     * Line width of the underline.
     * Set when currentlyUnderlined = true.
     */
    private double underlineLineWidth;

    /**
     * Y position of the last underlined element.
     */
    private double underlineY;

    /**
     * X position of the last underline line end.
     */
    private double underlineX;

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

    public Page getCurrentSourcePage() {
        return currentSourcePage;
    }

    public void setCurrentSourcePage(Page currentSourcePage) {
        this.currentSourcePage = currentSourcePage;
    }

    public int getCurrentSourceElementIndex() {
        return currentSourceElementIndex;
    }

    public void setCurrentSourceElementIndex(int currentSourceElementIndex) {
        this.currentSourceElementIndex = currentSourceElementIndex;
    }

    public boolean isCurrentlyUnderlined() {
        return currentlyUnderlined;
    }

    public void setCurrentlyUnderlined(boolean currentlyUnderlined) {
        this.currentlyUnderlined = currentlyUnderlined;
    }

    public double getUnderlineDescent() {
        return underlineDescent;
    }

    public void setUnderlineDescent(double underlineDescent) {
        this.underlineDescent = underlineDescent;
    }

    public double getUnderlineLineWidth() {
        return underlineLineWidth;
    }

    public void setUnderlineLineWidth(double underlineLineWidth) {
        this.underlineLineWidth = underlineLineWidth;
    }

    public double getUnderlineY() {
        return underlineY;
    }

    public void setUnderlineY(double underlineY) {
        this.underlineY = underlineY;
    }

    public double getUnderlineX() {
        return underlineX;
    }

    public void setUnderlineX(double underlineX) {
        this.underlineX = underlineX;
    }

    /**
     * Get the font variant from the passed node.
     *
     * @param node to get variant from
     * @return font variant
     */
    private FontVariant getFontVariantFromNode(Node node) {
        if (node instanceof FormattedNode) {
            Set<TextEmphasis> emphases = ((FormattedNode) node).getEmphases();

            boolean isBold = emphases.contains(TextEmphasis.BOLD);
            boolean isItalic = emphases.contains(TextEmphasis.ITALIC);

            if (isBold && isItalic) {
                return FontVariant.BOLD_ITALIC;
            } else if (isBold) {
                return FontVariant.BOLD;
            } else if (isItalic) {
                return FontVariant.ITALIC;
            }
        }

        return FontVariant.PLAIN;
    }

    /**
     * Get the font for the passed node.
     *
     * @param node to get font for
     * @return font
     */
    public PDFont getFontForNode(Node node) throws ExportException {
        String familyName = "Calibri"; // Default font family for testing

        FontVariant variant = getFontVariantFromNode(node);

        FontFamily family = FontManager.getInstance().getFamily(familyName).orElseThrow(() -> new ExportException(String.format(
                "Could not find font family '%s'",
                familyName
        )));

        FontVariantLocator locator = family.getVariantFont(variant).orElseThrow(() -> new ExportException(String.format(
                "Could not find font variant '%s' in font family '%s'",
                variant.name(),
                familyName
        )));

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
