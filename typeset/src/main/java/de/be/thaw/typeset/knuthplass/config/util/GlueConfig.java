package de.be.thaw.typeset.knuthplass.config.util;

/**
 * Configuration for the used glue.
 */
public interface GlueConfig {

    /**
     * Get the inter-word stretchability.
     *
     * @param lastChar of the last word to append glue to
     * @return stretchability
     */
    double getInterWordStretchability(char lastChar);

    /**
     * Get the inter-word shrinkability.
     *
     * @param lastChar of the last word to append glue to
     * @return shrinkability
     */
    double getInterWordShrinkability(char lastChar);

}
