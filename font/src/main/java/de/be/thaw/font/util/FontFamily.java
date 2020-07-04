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
    private final Map<FontVariant, FontDescriptor> variants;

    public FontFamily(Map<FontVariant, FontDescriptor> variants) {
        this.variants = variants;
    }

    /**
     * Get a font variant description.
     *
     * @return the font variant
     */
    public Optional<FontDescriptor> getVariantFont(FontVariant variant) {
        return Optional.ofNullable(variants.get(variant));
    }

}
