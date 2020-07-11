package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * Style of the background of an element.
 */
public class BackgroundStyle implements Style {

    /**
     * Background color.
     */
    private final ColorStyle color;

    public BackgroundStyle(
            ColorStyle color
    ) {
        this.color = color;
    }

    @Override
    public StyleType getType() {
        return StyleType.BACKGROUND;
    }

    @Override
    public Style merge(Style style) {
        if (style == null) {
            return this;
        }

        BackgroundStyle other = (BackgroundStyle) style;

        return new BackgroundStyle(
                getColor() != null ? (ColorStyle) getColor().merge(other.getColor()) : other.getColor()
        );
    }

    public ColorStyle getColor() {
        return color;
    }

}
