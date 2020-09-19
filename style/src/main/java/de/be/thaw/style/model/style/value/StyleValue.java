package de.be.thaw.style.model.style.value;

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
     * Get an integer representation of a value.
     * This has to be in the base unit (millimeter).
     *
     * @return integer representation
     */
    int intValue();

    /**
     * Get a double representation of a value.
     * This has to be in the base unit (millimeter).
     *
     * @return double representation
     */
    double doubleValue();

    /**
     * Get a color representation of a value.
     * For example "#FF0000", "rgb(1.0, 0, 0)" or "rgba(1.0, 0, 0, 1.0)" can be interpreted as color.
     *
     * @return color representation
     */
    Color colorValue();

}
