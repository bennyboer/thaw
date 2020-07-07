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
    private final double width;

    /**
     * Height value.
     */
    private final double height;

    public SizeStyle(
            double width,
            double height
    ) {
        this.width = width;
        this.height = height;
    }

    @Override
    public StyleType getType() {
        return StyleType.SIZE;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}
