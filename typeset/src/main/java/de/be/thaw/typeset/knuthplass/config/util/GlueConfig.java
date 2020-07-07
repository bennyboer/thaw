package de.be.thaw.typeset.knuthplass.config.util;

import de.be.thaw.core.document.node.DocumentNode;

/**
 * Configuration for the used glue.
 */
public interface GlueConfig {

    /**
     * Get the inter-word stretchability.
     *
     * @param node     the original node the glue is part of
     * @param lastChar of the last word to append glue to
     * @return stretchability
     * @throws Exception in case the inter word stretchability could not be determined
     */
    double getInterWordStretchability(DocumentNode node, char lastChar) throws Exception;

    /**
     * Get the inter-word shrinkability.
     *
     * @param node     the original node the glue is part of
     * @param lastChar of the last word to append glue to
     * @return shrinkability
     * @throws Exception in case the inter word shrinkability could not be determined
     */
    double getInterWordShrinkability(DocumentNode node, char lastChar) throws Exception;

}
