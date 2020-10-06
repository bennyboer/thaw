package de.be.thaw.style.model.style;

import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.value.StyleValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Collection to resolve styles.
 */
public class Styles {

    /**
     * List of blocks that are to be applied in the correct order to resolve styles properly.
     * First entry will be the first to check, then the second if the first does not contain
     * the searched property.
     */
    private final List<StyleBlock> blocks;

    /**
     * Map of overridden styles for this styles instance.
     */
    private Map<StyleType, StyleValue> overriddenStyles;

    public Styles(List<StyleBlock> blocks) {
        this.blocks = blocks;
    }

    /**
     * Copy constructor.
     *
     * @param styles to copy
     */
    public Styles(Styles styles) {
        blocks = styles.getBlocks();
    }

    /**
     * Resolve the given style type.
     *
     * @param type to resolve value for
     * @return style value
     */
    public Optional<StyleValue> resolve(StyleType type) {
        if (overriddenStyles != null) {
            StyleValue value = overriddenStyles.get(type);
            if (value != null) {
                return Optional.of(value);
            }
        }

        for (StyleBlock block : blocks) {
            StyleValue value = block.getStyles().get(type);
            if (value != null) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    /**
     * Override a style - only for this styles instance.
     *
     * @param type  of the style
     * @param value to set
     */
    public void overrideStyle(StyleType type, StyleValue value) {
        if (overriddenStyles == null) {
            overriddenStyles = new HashMap<>();
        }

        overriddenStyles.put(type, value);
    }

    /**
     * Get the style blocks.
     *
     * @return style blocks
     */
    public List<StyleBlock> getBlocks() {
        return blocks;
    }

}
