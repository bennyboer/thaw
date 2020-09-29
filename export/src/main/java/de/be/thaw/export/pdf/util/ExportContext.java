package de.be.thaw.export.pdf.util;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.PdfExporter;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.export.pdf.font.exception.FontParseException;
import de.be.thaw.font.ThawFont;
import de.be.thaw.font.util.FontFamily;
import de.be.thaw.font.util.FontManager;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.FontVariantLocator;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.font.util.exception.FontRegisterException;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.FontStyleValue;
import de.be.thaw.style.model.style.value.FontVariantStyleValue;
import de.be.thaw.style.model.style.value.KerningModeStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Size;
import de.be.thaw.util.unit.Unit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Context used during PDF export.
 */
public class ExportContext {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = Logger.getLogger(PdfExporter.class.getSimpleName());

    /**
     * Cache for already loaded and embedded fonts.
     */
    private final Map<FontVariantLocator, ThawFont> fontCache = new HashMap<>();

    /**
     * The PDF document to export to.
     */
    private final PDDocument pdDocument;

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

    /**
     * The source document.
     */
    private final Document document;

    /**
     * Lookup of elements by their original DocumentNode ID.
     */
    private Map<String, ElementLocator> elementLookup;

    /**
     * The math font to use.
     */
    private ThawFont mathFont;

    public ExportContext(PDDocument pdDocument, Document document) {
        this.pdDocument = pdDocument;
        this.document = document;
    }

    /**
     * Get the PDF document to export to.
     *
     * @return document
     */
    public PDDocument getPdDocument() {
        return pdDocument;
    }

    /**
     * Get the source document.
     *
     * @return source document
     */
    public Document getDocument() {
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
    private FontVariant getFontVariantFromNode(DocumentNode node) {
        FontVariant variant = node.getStyles()
                .resolve(StyleType.FONT_VARIANT)
                .orElse(new FontVariantStyleValue(FontVariant.PLAIN))
                .fontVariant();

        if (node.getTextNode() instanceof FormattedNode) {
            Set<TextEmphasis> emphases = ((FormattedNode) node.getTextNode()).getEmphases();

            boolean isBold = emphases.contains(TextEmphasis.BOLD);
            boolean isItalic = emphases.contains(TextEmphasis.ITALIC);
            boolean isMonospace = emphases.contains(TextEmphasis.CODE);

            if (isBold && isItalic) {
                return FontVariant.BOLD_ITALIC;
            } else if (isBold) {
                return FontVariant.BOLD;
            } else if (isItalic) {
                return FontVariant.ITALIC;
            } else if (isMonospace) {
                return FontVariant.MONOSPACE;
            }
        }

        return variant;
    }

    /**
     * Get the font variant locator for the passed style value (FontStyleValue)
     * and the preferred font variant.
     *
     * @param value   to get locator for
     * @param variant the preferred variant
     * @param node    of the style value
     * @return locator
     * @throws ExportException in case the variant locator could not be resolved
     */
    private FontVariantLocator getFontVariantLocatorForStyleValue(StyleValue value, FontVariant variant, DocumentNode node) throws ExportException {
        if (!(value instanceof FontStyleValue)) {
            throw new ExportException("Expected style value to fetch font from to be of type FontStyleValue");
        }

        FontVariantLocator locator;
        boolean overrideVariant = false;
        if (value.file() != null) {
            // Is font family folder or file
            File fontFile = value.file();

            try {
                if (fontFile.isDirectory()) {
                    // Is a folder of font files (multiple font variants)
                    locator = FontManager.getInstance().registerFontFolder(fontFile).get(0);
                } else {
                    // Is only one font file (one font variant) -> we ignore the wanted font variant and just use the font specified
                    locator = FontManager.getInstance().registerFont(fontFile).get(0);

                    // Ignore the set font variant
                    overrideVariant = true;
                }
            } catch (FontRegisterException e) {
                throw new ExportException(e);
            }
        } else {
            // Is a font family name -> search in fonts installed in the OS
            String familyName = value.value();

            FontFamily family = FontManager.getInstance().getFamily(familyName).orElseThrow(() -> new ExportException(String.format(
                    "Could not find font family '%s' in OS installed fonts. " +
                            "If on Windows make sure that the fonts were installed for all users for Thaw to be able to pick it up.",
                    familyName
            )));

            Optional<FontVariantLocator> optionalLocator = family.getVariantFont(variant);
            if (optionalLocator.isPresent()) {
                locator = optionalLocator.get();
            } else {
                if (variant == FontVariant.MONOSPACE) {
                    StyleValue monospacedFontFamilyStyleValue = node.getStyles().resolve(StyleType.INLINE_CODE_FONT_FAMILY).orElseThrow();
                    return getFontVariantLocatorForStyleValue(monospacedFontFamilyStyleValue, FontVariant.MONOSPACE, node);
                } else {
                    throw new ExportException(String.format(
                            "Could not find font variant '%s' in font family '%s'",
                            variant.name(),
                            familyName
                    ));
                }
            }
        }

        if (locator.getVariant() != variant) {
            if (overrideVariant) {
                return locator;
            } else {
                throw new ExportException(String.format(
                        "Could not find font of family '%s' for variant '%s'",
                        locator.getFamilyName(),
                        variant.name()
                ));
            }
        }

        return locator;
    }

    /**
     * Get the font for the passed node.
     *
     * @param node to get font for
     * @return font
     */
    public ThawFont getFontForNode(DocumentNode node) throws ExportException {
        FontVariantLocator locator = getFontVariantLocatorForStyleValue(
                node.getStyles().resolve(StyleType.FONT_FAMILY).orElseThrow(),
                getFontVariantFromNode(node),
                node
        );

        ThawPdfFont font = (ThawPdfFont) fontCache.get(locator);
        if (font == null) {
            // Load the font
            try {
                font = new ThawPdfFont(locator.getFontName(), new File(locator.getFontFile().getLocation()), getPdDocument());

                fontCache.put(locator, font);
            } catch (FontParseException e) {
                throw new ExportException(e);
            }
        }

        if (locator.getVariant() != FontVariant.MONOSPACE) { // Monospaced fonts should not be optically kerned
            font.setKerningMode(node.getStyles()
                    .resolve(StyleType.FONT_KERNING)
                    .orElse(new KerningModeStyleValue(KerningMode.NATIVE))
                    .kerningMode());
        }

        return font;
    }

    /**
     * Get the font size for the passed node.
     *
     * @param node to get font size for
     * @return font size
     */
    public double getFontSizeForNode(DocumentNode node) {
        return node.getStyles().resolve(StyleType.FONT_SIZE)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
    }

    /**
     * Get the element lookup by the original DocumentNode ID.
     *
     * @return element lookup
     */
    public Map<String, ElementLocator> getElementLookup() {
        return elementLookup;
    }

    /**
     * Set the element lookup by the original DocumentNode ID.
     *
     * @param elementLookup to set
     */
    public void setElementLookup(Map<String, ElementLocator> elementLookup) {
        this.elementLookup = elementLookup;
    }

    /**
     * Set the math font to use.
     *
     * @param mathFont to set
     */
    public void setMathFont(ThawFont mathFont) {
        this.mathFont = mathFont;
    }

    /**
     * Get the math font to use.
     *
     * @return math font
     */
    public ThawFont getMathFont() {
        return mathFont;
    }

}
