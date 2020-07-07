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
    private final Double top;

    /**
     * Inset from left.
     */
    private final Double left;

    /**
     * Inset from bottom.
     */
    private final Double bottom;

    /**
     * Inset from right;
     */
    private final Double right;

    public InsetsStyle(
            Double top,
            Double left,
            Double bottom,
            Double right
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

    @Override
    public Style merge(Style style) {
        if (style == null) {
            return this;
        }

        InsetsStyle other = (InsetsStyle) style;

        return new InsetsStyle(
                top != null ? top : other.getTop(),
                left != null ? left : other.getLeft(),
                bottom != null ? bottom : other.getBottom(),
                right != null ? right : other.getRight()
        );
    }

    public Double getTop() {
        return top;
    }

    public Double getLeft() {
        return left;
    }

    public Double getBottom() {
        return bottom;
    }

    public Double getRight() {
        return right;
    }

}
