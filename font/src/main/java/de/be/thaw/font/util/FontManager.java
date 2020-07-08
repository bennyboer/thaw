package de.be.thaw.font.util;

import de.be.thaw.font.system.SystemFontManager;
import de.be.thaw.font.util.exception.CouldNotDetermineFontVariantException;
import de.be.thaw.font.util.exception.CouldNotGetFontsException;
import de.be.thaw.font.util.file.FontCollectionFile;
import de.be.thaw.font.util.file.FontFile;
import de.be.thaw.font.util.file.SingleFontFile;

import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
     * Mapping of font variants to families that support that variant.
     */
    private final Map<FontVariant, List<FontFamily>> variantSupport = new HashMap<>();

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
     * Get all families that support the passed variant.
     *
     * @param variant to check supporting families for
     * @return supporting families
     */
    public List<FontFamily> getFamiliesSupportingVariant(FontVariant variant) {
        List<FontFamily> families = variantSupport.get(variant);
        if (families == null) {
            return Collections.emptyList();
        }

        return families;
    }

    /**
     * Initialize the available font families.
     */
    private void initFamilies() {
        FontFile[] fontFiles;
        try {
            fontFiles = SystemFontManager.getAvailableFonts();
        } catch (CouldNotGetFontsException e) {
            throw new IllegalStateException("Could not get any fonts from the system");
        }

        Map<String, Map<FontVariant, FontVariantLocator>> familyMapping = new HashMap<>();
        for (FontFile file : fontFiles) {
            if (file.isCollection()) {
                FontCollectionFile fcf = (FontCollectionFile) file;

                for (Font font : fcf.getFonts()) {
                    addFontToMapping(font, fcf, familyMapping);
                }
            } else {
                SingleFontFile sff = (SingleFontFile) file;

                addFontToMapping(sff.getFont(), sff, familyMapping);
            }
        }

        for (Map.Entry<String, Map<FontVariant, FontVariantLocator>> entry : familyMapping.entrySet()) {
            FontFamily family = new FontFamily(entry.getKey(), entry.getValue());

            families.put(entry.getKey(), family);

            for (FontVariant variant : entry.getValue().keySet()) {
                variantSupport.computeIfAbsent(variant, k -> new ArrayList<>()).add(family);
            }
        }
    }

    /**
     * Add the passed font to the given mapping.
     *
     * @param font          to add
     * @param file          the font belongs to
     * @param familyMapping to add font to
     */
    private void addFontToMapping(Font font, FontFile file, Map<String, Map<FontVariant, FontVariantLocator>> familyMapping) {
        String familyName = font.getFamily();

        try {
            FontVariant variant = getVariantForFont(font);

            Map<FontVariant, FontVariantLocator> currentMapping = familyMapping.computeIfAbsent(familyName, (k) -> new HashMap<>());
            if (!currentMapping.containsKey(variant)) {
                // Do not overwrite existing variants
                currentMapping.put(variant, new FontVariantLocator(file, font.getFontName()));
            }
        } catch (CouldNotDetermineFontVariantException e) {
            System.out.println(String.format("[WARN] '%s'", e.getMessage()));
        }
    }

    /**
     * Check if the passed font is monospaced.
     *
     * @param font to check
     * @return whether monospaced
     */
    private boolean isMonospaced(Font font) {
        FontRenderContext frc = new FontRenderContext(null, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);

        Rectangle2D iBounds = font.getStringBounds("i", frc);
        Rectangle2D mBounds = font.getStringBounds("m", frc);

        return iBounds.getWidth() == mBounds.getWidth();
    }

    /**
     * Get the font variant for the passed font.
     *
     * @param font to get variant for
     * @return the font variant
     * @throws CouldNotDetermineFontVariantException in case the font variant could not be determined
     */
    private FontVariant getVariantForFont(Font font) throws CouldNotDetermineFontVariantException {
        // Try to guess the variant by the font name
        String name = font.getFontName();
        if (name.startsWith(font.getFamily())) {
            name = name.substring(font.getFamily().length());
        }

        boolean isMonospaced = isMonospaced(font);

        if (name.isBlank()) {
            return isMonospaced ? FontVariant.MONOSPACE : FontVariant.PLAIN;
        }

        String originalFontVariant = name.trim();

        name = name.toLowerCase();

        int regularIndex = name.indexOf("regular");
        if (regularIndex != -1) {
            return FontVariant.PLAIN;
        }

        int boldIndex = name.indexOf("bold");
        if (boldIndex != -1) {
            name = name.substring(0, boldIndex) + name.substring(boldIndex + "bold".length());
        }

        int italicIndex = name.indexOf("italic");
        if (italicIndex != -1) {
            name = name.substring(0, italicIndex) + name.substring(italicIndex + "italic".length());
        }

        if (name.isBlank()) {
            // There are no more font variants we do not support (yet) -> valid font variant for our uses!
            if (boldIndex != -1 && italicIndex != -1) {
                return FontVariant.BOLD_ITALIC;
            } else if (boldIndex != -1) {
                return FontVariant.BOLD;
            } else if (italicIndex != -1) {
                return FontVariant.ITALIC;
            }
        }

        throw new CouldNotDetermineFontVariantException(String.format(
                "The font variant string '%s' could not be mapped to a known font variant",
                originalFontVariant
        ));
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
