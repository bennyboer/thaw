package de.be.thaw.typeset.knuthplass.config.util;

import de.be.thaw.text.model.tree.Node;

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
    double getInterWordStretchability(Node node, char lastChar) throws Exception;

    /**
     * Get the inter-word shrinkability.
     *
     * @param node     the original node the glue is part of
     * @param lastChar of the last word to append glue to
     * @return shrinkability
     * @throws Exception in case the inter word shrinkability could not be determined
     */
    double getInterWordShrinkability(Node node, char lastChar) throws Exception;

}
