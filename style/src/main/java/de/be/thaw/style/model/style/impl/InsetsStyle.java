package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * Style specifying insets.
 */
public class InsetsStyle implements Style {

    /**
     * Inset from top.
     */
    private final double top;

    /**
     * Inset from left.
     */
    private final double left;

    /**
     * Inset from bottom.
     */
    private final double bottom;

    /**
     * Inset from right;
     */
    private final double right;

    public InsetsStyle(
            double top,
            double left,
            double bottom,
            double right
    ) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    @Override
    public StyleType getType() {
        return StyleType.INSETS;
    }

    public double getTop() {
        return top;
    }

    public double getLeft() {
        return left;
    }

    public double getBottom() {
        return bottom;
    }

    public double getRight() {
        return right;
    }

}
