package de.be.thaw.style.model.style.impl;

import de.be.thaw.font.util.FontVariant;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * Styles applicable to an elements used font.
 */
public class FontStyle implements Style {

    /**
     * Font family.
     */
    private final String family;

    /**
     * Variant of the font.
     */
    private final FontVariant variant;

    /**
     * Size of the font.
     */
    private final Double size;

    /**
     * Color of the font.
     */
    private final ColorStyle color;

    public FontStyle(
            String family,
            FontVariant variant,
            Double size,
            ColorStyle color
    ) {
        this.family = family;
        this.variant = variant;
        this.size = size;
        this.color = color;
    }

    @Override
    public StyleType getType() {
        return StyleType.FONT;
    }

    @Override
    public Style merge(Style style) {
        if (style == null) {
            return this;
        }

        FontStyle other = (FontStyle) style;

        return new FontStyle(
                family != null ? family : other.getFamily(),
                variant != null ? variant : other.getVariant(),
                size != null ? size : other.getSize(),
                color != null ? color : other.getColor()
        );
    }

    public String getFamily() {
        return family;
    }

    public FontVariant getVariant() {
        return variant;
    }

    public Double getSize() {
        return size;
    }

    public ColorStyle getColor() {
        return color;
    }

}
