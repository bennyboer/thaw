package de.be.thaw.style.model.style.value;

import de.be.thaw.util.color.Color;

/**
 * Value of type double.
 */
public class ColorStyleValue extends AbstractStyleValue {

    /**
     * The color value.
     */
    private final Color color;

    public ColorStyleValue(Color color) {
        this.color = color;
    }

    @Override
    public String value() {
        return color.toString();
    }

    @Override
    public Color colorValue() {
        return color;
    }

}
