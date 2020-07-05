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
     * @throws Exception in case the code width could not be determined
     */
    double getCodeWidth(Node node, int code) throws Exception;

    /**
     * Get the width of the passed string.
     *
     * @param node the passed string belongs to
     * @param str  to get width for
     * @return width
     * @throws Exception in case the string width could not be determined
     */
    double getStringWidth(Node node, String str) throws Exception;

    /**
     * Get the space width that applies to the passed node.
     *
     * @param node to get space width for
     * @return width of a space
     * @throws Exception in case the space width could not be determined
     */
    double getSpaceWidth(Node node) throws Exception;

}
