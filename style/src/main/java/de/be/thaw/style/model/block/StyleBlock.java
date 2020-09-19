package de.be.thaw.style.model.block;

import de.be.thaw.style.model.selector.StyleSelector;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.StyleValue;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a style block.
 */
public class StyleBlock {

    /**
     * Selector of the block.
     * It defines to what to apply the styles defined in the block.
     */
    private final StyleSelector selector;

    /**
     * Styles in the block.
     */
    private final Map<StyleType, StyleValue> styles;

    public StyleBlock(StyleSelector selector, Map<StyleType, StyleValue> styles) {
        this.selector = selector;
        this.styles = styles;
    }

    /**
     * Get the selector of the block.
     *
     * @return selector
     */
    public StyleSelector getSelector() {
        return selector;
    }

    /**
     * Get the styles in the block.
     *
     * @return styles
     */
    public Map<StyleType, StyleValue> getStyles() {
        return styles;
    }

    /**
     * Merge this style block with the passed one.
     *
     * @param other style block to merge
     * @return the merged style block
     */
    public StyleBlock merge(@Nullable StyleBlock other) {
        if (other == null) {
            return this;
        }

        Map<StyleType, StyleValue> mergedStyles = new HashMap<>();

        // Copy styles in this block in the new map
        for (Map.Entry<StyleType, StyleValue> styleEntry : getStyles().entrySet()) {
            mergedStyles.put(styleEntry.getKey(), styleEntry.getValue());
        }

        // Merge remaining styles from the other block (if any)
        for (Map.Entry<StyleType, StyleValue> styleEntry : other.getStyles().entrySet()) {
            if (!mergedStyles.containsKey(styleEntry.getKey())) {
                mergedStyles.put(styleEntry.getKey(), styleEntry.getValue());
            }
        }

        return new StyleBlock(getSelector(), mergedStyles);
    }

}
