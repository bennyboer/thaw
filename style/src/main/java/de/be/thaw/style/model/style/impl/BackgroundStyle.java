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

    public ColorStyle getColor() {
        return color;
    }

}
