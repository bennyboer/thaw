package de.be.thaw.typeset.knuthplass.config.util;

import de.be.thaw.core.document.node.DocumentNode;

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
    double getCodeWidth(DocumentNode node, int code) throws Exception;

    /**
     * Get the width of the passed string.
     *
     * @param node the passed string belongs to
     * @param str  to get width for
     * @return width
     * @throws Exception in case the string width could not be determined
     */
    double getStringWidth(DocumentNode node, String str) throws Exception;

    /**
     * Get the space width that applies to the passed node.
     *
     * @param node to get space width for
     * @return width of a space
     * @throws Exception in case the space width could not be determined
     */
    double getSpaceWidth(DocumentNode node) throws Exception;

    /**
     * Get the leading for the font of the passed node.
     *
     * @param node to get leading
     * @return leading
     * @throws Exception in case the leading
     */
    double getLineHeight(DocumentNode node) throws Exception;

}
