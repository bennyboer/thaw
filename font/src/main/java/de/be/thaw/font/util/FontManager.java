package de.be.thaw.font.util;

import de.be.thaw.font.system.SystemFontManager;
import de.be.thaw.font.util.exception.CouldNotDetermineFontVariantException;
import de.be.thaw.font.util.exception.CouldNotGetFontsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Manager managing available fonts.
 */
public class FontManager {

    /**
     * Mapping of font families to their font files.
     */
    private final Map<String, FontFamily> families = new HashMap<>();

    /**
     * Get the current instance of the font manager.
     *
     * @return instance
     */
    public static FontManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private FontManager() {
        initFamilies();
    }

    /**
     * Get all available font families.
     *
     * @return font families
     */
    public Set<String> getFontFamilies() {
        return families.keySet();
    }

    /**
     * Get a font family by its name.
     *
     * @param name of the font family
     * @return font family
     */
    public Optional<FontFamily> getFamily(String name) {
        return Optional.ofNullable(families.get(name));
    }

    /**
     * Initialize the available font families.
     */
    private void initFamilies() {
        FontDescriptor[] descriptors;
        try {
            descriptors = SystemFontManager.getAvailableFonts();
        } catch (CouldNotGetFontsException e) {
            throw new IllegalStateException("Could not get any fonts from the system");
        }

        Map<String, Map<FontVariant, FontDescriptor>> familyMapping = new HashMap<>();
        for (FontDescriptor descriptor : descriptors) {
            String familyName = descriptor.getFont().getFamily();

            FontVariant variant;
            try {
                variant = getVariantForDescriptor(descriptor);
            } catch (CouldNotDetermineFontVariantException e) {
                variant = FontVariant.PLAIN;
            }

            familyMapping.computeIfAbsent(familyName, (k) -> new HashMap<>()).put(variant, descriptor);
        }

        for (Map.Entry<String, Map<FontVariant, FontDescriptor>> entry : familyMapping.entrySet()) {
            families.put(entry.getKey(), new FontFamily(entry.getValue()));
        }
    }

    /**
     * Get the font variant for the passed font descriptor.
     *
     * @param descriptor to get variant for
     * @return the font variant
     * @throws CouldNotDetermineFontVariantException in case the font variant could not be determined
     */
    private FontVariant getVariantForDescriptor(FontDescriptor descriptor) throws CouldNotDetermineFontVariantException {
        FontVariant variant = null;

        if (descriptor.getFont().isPlain()) {
            variant = FontVariant.PLAIN;
        } else if (descriptor.getFont().isBold()) {
            if (descriptor.getFont().isItalic()) {
                variant = FontVariant.BOLD_ITALIC;
            } else {
                variant = FontVariant.ITALIC;
            }
        } else if (descriptor.getFont().isItalic()) {
            variant = FontVariant.ITALIC;
        }

        if (variant == null || variant == FontVariant.PLAIN) {
            // Try to guess the variant by the font name
            String name = descriptor.getFont().getFontName().toLowerCase();

            System.out.println(name);

            boolean isBold = name.contains("bold");
            boolean isItalic = name.contains("italic");
        }

        if (variant == null) {
            throw new CouldNotDetermineFontVariantException(String.format(
                    "Could not determine the font variant for font at '%s'",
                    descriptor.getLocation()
            ));
        }

        return variant;
    }

    /**
     * Holder of the font manager singleton instance.
     */
    private static final class InstanceHolder {

        /**
         * Instance of the font manager.
         */
        static final FontManager INSTANCE = new FontManager();

    }

}
