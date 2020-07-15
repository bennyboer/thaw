package de.be.thaw.style.model.block;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

import java.util.HashMap;
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

    /**
     * Merge this style block with the passed one.
     *
     * @param other style block to merge
     * @return the merged style block
     */
    public StyleBlock merge(StyleBlock other) {
        if (other == null) {
            return this;
        }

        Map<StyleType, Style> mergedStyles = new HashMap<>();
        for (Map.Entry<StyleType, Style> styleEntry : getStyles().entrySet()) {
            mergedStyles.put(styleEntry.getKey(), styleEntry.getValue().merge(other.getStyles().get(styleEntry.getKey())));
        }

        // Merge remaining styles from the other block (if any)
        for (Map.Entry<StyleType, Style> styleEntry : other.getStyles().entrySet()) {
            if (!mergedStyles.containsKey(styleEntry.getKey())) {
                mergedStyles.put(styleEntry.getKey(), styleEntry.getValue());
            }
        }

        return new StyleBlock(getName(), mergedStyles);
    }

}
