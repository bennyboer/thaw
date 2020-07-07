package de.be.thaw.core.document.node.style;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Style of a document node.
 */
public class DocumentNodeStyle {

    /**
     * The parent document node style (if any).
     */
    private final DocumentNodeStyle parent;

    /**
     * Styles applied directly on the node.
     */
    private final Map<StyleType, Style> styles;

    public DocumentNodeStyle(DocumentNodeStyle parent, Map<StyleType, Style> styles) {
        this.parent = parent;
        this.styles = styles;
    }

    /**
     * Get a style attribute by the passed style type and lookup function.
     *
     * @param type           to get style for
     * @param lookupFunction to get concrete attribute with
     * @return style
     */
    public <T> Optional<T> getStyleAttribute(StyleType type, Function<Style, Optional<T>> lookupFunction) {
        // Check first if the style is set directly on the node (if not continue searching in parent).
        DocumentNodeStyle node = this;
        while (node != null) {
            Style style = null;
            if (node.styles != null) {
                style = node.styles.get(type);
            }

            if (style != null) {
                // Check if style attribute is set
                Optional<T> result = lookupFunction.apply(style);
                if (result.isPresent()) {
                    return result;
                }
            }

            // Check in parent next iteration
            node = node.parent;
        }

        return Optional.empty(); // Could not find style attribute
    }

}
