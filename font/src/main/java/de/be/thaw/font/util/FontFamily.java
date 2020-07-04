package de.be.thaw.font.util;

import java.util.Map;
import java.util.Optional;

/**
 * Representation of a font family.
 */
public class FontFamily {

    /**
     * Mapping of the different font variants to their actual font files.
     */
    private final Map<FontVariant, FontVariantLocator> variants;

    public FontFamily(Map<FontVariant, FontVariantLocator> variants) {
        this.variants = variants;
    }

    /**
     * Get a font variant locator used to get the correct font.
     *
     * @return the font variant locator
     */
    public Optional<FontVariantLocator> getVariantFont(FontVariant variant) {
        return Optional.ofNullable(variants.get(variant));
    }

}
