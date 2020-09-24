package de.be.thaw.style.model.style.value;

import de.be.thaw.style.model.style.StyleType;

import java.util.Map;

/**
 * A collection of style values.
 */
public class StyleValueCollection extends AbstractStyleValue {

    /**
     * Styles in the collection.
     */
    private final Map<StyleType, StyleValue> styles;

    public StyleValueCollection(Map<StyleType, StyleValue> styles) {
        this.styles = styles;
    }

    /**
     * Get the styles in the collection.
     *
     * @return styles
     */
    public Map<StyleType, StyleValue> getStyles() {
        return styles;
    }

    @Override
    public String value() {
        throw new UnsupportedOperationException();
    }

}
