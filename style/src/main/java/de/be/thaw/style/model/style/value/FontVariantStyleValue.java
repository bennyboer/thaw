package de.be.thaw.style.model.style.value;

import de.be.thaw.font.util.FontVariant;

/**
 * Value of type font variant.
 */
public class FontVariantStyleValue extends AbstractStyleValue {

    /**
     * The varian value.
     */
    private final FontVariant variant;

    public FontVariantStyleValue(FontVariant color) {
        this.variant = color;
    }

    @Override
    public String value() {
        return variant.name().toLowerCase();
    }

    @Override
    public FontVariant fontVariant() {
        return variant;
    }

}
