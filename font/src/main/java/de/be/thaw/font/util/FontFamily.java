package de.be.thaw.font.util;

import java.util.Map;
import java.util.Optional;

/**
 * Representation of a font family.
 */
public class FontFamily {

    /**
     * The family name.
     */
    private final String name;

    /**
     * Mapping of the different font variants to their actual font files.
     */
    private final Map<FontVariant, FontVariantLocator> variants;

    public FontFamily(String name, Map<FontVariant, FontVariantLocator> variants) {
        this.name = name;
        this.variants = variants;
    }

    /**
     * Get the family name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get a font variant locator used to get the correct font.
     *
     * @return the font variant locator
     */
    public Optional<FontVariantLocator> getVariantFont(FontVariant variant) {
        return Optional.ofNullable(variants.get(variant));
    }

    /**
     * Add a variant to the font family.
     *
     * @param variant to add
     * @param locator of the font variant
     * @return whether the font variant has been added to the family (true) or does already exist (false).
     */
    public boolean addVariant(FontVariant variant, FontVariantLocator locator) {
        FontVariantLocator existingLocator = variants.get(variant);
        if (existingLocator != null) {
            return false;
        } else {
            variants.put(variant, locator);
            return true;
        }
    }

}
