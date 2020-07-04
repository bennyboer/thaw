package de.be.thaw.font.util;

import de.be.thaw.font.util.file.FontFile;

/**
 * Locator of a font variant.
 */
public class FontVariantLocator {

    /**
     * The font file the font variant is contained in.
     */
    private final FontFile fontFile;

    /**
     * Name of the font in the font file.
     * This is needed because font collection files may contain more than one font!
     */
    private final String fontName;

    public FontVariantLocator(FontFile fontFile, String fontName) {
        this.fontFile = fontFile;
        this.fontName = fontName;
    }

    /**
     * Get the font file.
     *
     * @return font file
     */
    public FontFile getFontFile() {
        return fontFile;
    }

    /**
     * Get the name of the font contained in the font file.
     *
     * @return font name
     */
    public String getFontName() {
        return fontName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FontVariantLocator locator = (FontVariantLocator) o;

        if (!fontFile.getLocation().equals(locator.fontFile.getLocation())) return false;
        return fontName.equals(locator.fontName);
    }

    @Override
    public int hashCode() {
        int result = fontFile.getLocation().hashCode();
        result = 31 * result + fontName.hashCode();
        return result;
    }

}
