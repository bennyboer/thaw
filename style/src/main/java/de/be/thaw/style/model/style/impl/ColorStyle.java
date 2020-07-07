package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * A color style.
 */
public class ColorStyle implements Style {

    /**
     * Red share.
     */
    private final Double red;

    /**
     * Green share.
     */
    private final Double green;

    /**
     * Blue share.
     */
    private final Double blue;

    /**
     * Alpha share.
     */
    private final Double alpha;

    public ColorStyle(
            Double red,
            Double green,
            Double blue,
            Double alpha
    ) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    @Override
    public StyleType getType() {
        return StyleType.COLOR;
    }

    @Override
    public Style merge(Style style) {
        if (style == null) {
            return this;
        }

        ColorStyle other = (ColorStyle) style;

        return new ColorStyle(
                red != null ? red : other.getRed(),
                green != null ? green : other.getGreen(),
                blue != null ? blue : other.getBlue(),
                alpha != null ? alpha : other.getAlpha()
        );
    }

    public Double getRed() {
        return red;
    }

    public Double getGreen() {
        return green;
    }

    public Double getBlue() {
        return blue;
    }

    public Double getAlpha() {
        return alpha;
    }

}
