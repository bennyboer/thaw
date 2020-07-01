package de.be.thaw.typeset.knuthplass.config.util;

import de.be.thaw.text.model.tree.Node;

/**
 * Supplier for font details needed to properly typeset text.
 */
public interface FontDetailsSupplier {

    /**
     * Get the width of the passed character code.
     *
     * @param node of the text the passed character belongs to
     * @param code to get width for
     */
    double getCodeWidth(Node node, int code);

    /**
     * Get the width of the passed string.
     *
     * @param node the passed string belongs to
     * @param str  to get width for
     * @return width
     */
    double getStringWidth(Node node, String str);

    /**
     * Get the space width that applies to the passed node.
     *
     * @param node to get space width for
     * @return width of a space
     */
    double getSpaceWidth(Node node);

}
