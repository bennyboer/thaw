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
     */
    double getInterWordStretchability(Node node, char lastChar);

    /**
     * Get the inter-word shrinkability.
     *
     * @param node     the original node the glue is part of
     * @param lastChar of the last word to append glue to
     * @return shrinkability
     */
    double getInterWordShrinkability(Node node, char lastChar);

}
