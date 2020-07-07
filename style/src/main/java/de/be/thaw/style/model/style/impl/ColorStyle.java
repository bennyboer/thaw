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
    private final double red;

    /**
     * Green share.
     */
    private final double green;

    /**
     * Blue share.
     */
    private final double blue;

    /**
     * Alpha share.
     */
    private final double alpha;

    public ColorStyle(
            double red,
            double green,
            double blue,
            double alpha
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

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public double getAlpha() {
        return alpha;
    }

}
