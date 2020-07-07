package de.be.thaw.typeset.knuthplass.config.util;

import de.be.thaw.core.document.node.DocumentNode;

/**
 * Supplier for font details needed to properly typeset text.
 */
public interface FontDetailsSupplier {

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

}
