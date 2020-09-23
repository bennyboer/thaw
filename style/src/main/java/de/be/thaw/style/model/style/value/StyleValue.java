package de.be.thaw.style.model.style.value;

import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.style.util.FillStyle;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.unit.Unit;

import java.util.Optional;

/**
 * Representation of a style value.
 */
public interface StyleValue {

    /**
     * Get the original string value of the style.
     *
     * @return string value
     */
    String value();

    /**
     * Get a boolean representation of a value.
     *
     * @return boolean representation
     */
    boolean booleanValue();

    /**
     * Get an integer representation of a value.
     * The value is given in the unit supplied by calling unit() on this object.
     *
     * @return integer representation
     */
    int intValue();

    /**
     * Get a double representation of a value.
     * The value is given in the unit supplied by calling unit() on this object.
     *
     * @return double representation
     */
    double doubleValue();

    /**
     * Get the unit of the value (if the value type supports this).
     *
     * @return unit
     */
    Optional<Unit> unit();

    /**
     * Get a color representation of a value.
     * For example "#FF0000", "rgb(1.0, 0, 0)" or "rgba(1.0, 0, 0, 1.0)" can be interpreted as color.
     *
     * @return color representation
     */
    Color colorValue();

    /**
     * Get the horizontal alignment this values expresses.
     *
     * @return horizontal alignment
     */
    HorizontalAlignment horizontalAlignment();

    /**
     * Get the font variant representation from this style value.
     *
     * @return font variant
     */
    FontVariant fontVariant();

    /**
     * Get the kerning mode representation from this style value.
     *
     * @return kerning mode
     */
    KerningMode kerningMode();

    /**
     * Get the fill style representation from this style value.
     *
     * @return fill style
     */
    FillStyle fillStyle();

}
