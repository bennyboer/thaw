package de.be.thaw.style.model.block;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

import java.util.Map;

/**
 * Representation of a style block.
 */
public class StyleBlock {

    /**
     * Name of the block.
     */
    private final String name;

    /**
     * Styles in the block.
     */
    private final Map<StyleType, Style> styles;

    public StyleBlock(String name, Map<StyleType, Style> styles) {
        this.name = name;
        this.styles = styles;
    }

    public String getName() {
        return name;
    }

    public Map<StyleType, Style> getStyles() {
        return styles;
    }

}
