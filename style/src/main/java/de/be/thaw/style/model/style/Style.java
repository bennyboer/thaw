package de.be.thaw.style.model.style;

/**
 * Style of the style model.
 */
public interface Style {

    /**
     * Get the style type.
     *
     * @return type
     */
    StyleType getType();

    /**
     * Merge with another style of the same type.
     *
     * @param style to merge with
     * @return the merged style
     */
    Style merge(Style style);

}
