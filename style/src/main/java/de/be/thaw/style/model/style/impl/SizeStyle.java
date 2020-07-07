package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * Style specifing the size of something.
 */
public class SizeStyle implements Style {

    /**
     * Width value.
     */
    private final Double width;

    /**
     * Height value.
     */
    private final Double height;

    public SizeStyle(
            Double width,
            Double height
    ) {
        this.width = width;
        this.height = height;
    }

    @Override
    public StyleType getType() {
        return StyleType.SIZE;
    }

    @Override
    public Style merge(Style style) {
        if (style == null) {
            return this;
        }

        SizeStyle other = (SizeStyle) style;

        return new SizeStyle(
                width != null ? width : other.getWidth(),
                height != null ? height : other.getHeight()
        );
    }

    public Double getWidth() {
        return width;
    }

    public Double getHeight() {
        return height;
    }

}
