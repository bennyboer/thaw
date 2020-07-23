package de.be.thaw.typeset.knuthplass.config;

import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSourceSupplier;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Size;

/**
 * Configuration of the Knuth-Plass line breaking algorithm.
 */
public class KnuthPlassTypeSettingConfig {

    /**
     * Width of indentation.
     */
    private final double indentWidth;

    /**
     * Size of the de.be.thaw.typeset.page to typeset on (in arbitrary units).
     */
    private final Size pageSize;

    /**
     * Insets of the pages.
     */
    private final Insets pageInsets;

    /**
     * Defines how many lines more than the optimum value are allowed.
     */
    private final int looseness;

    /**
     * Tolerance of the badness.
     * This value is the maximum adjustment ratio allowed to fit a line.
     */
    private final double tolerance;

    /**
     * Demerit added when the break points of two consecutive lines
     * are flagged.
     */
    private final double flaggedDemerit;

    /**
     * Demerit added when two consecutive lines have different
     * fitness classes.
     */
    private final double fitnessDemerit;

    /**
     * Supplier for font details.
     */
    private final FontDetailsSupplier fontDetailsSupplier;

    /**
     * Hyphenator used to hyphenate words.
     */
    private final Hyphenator hyphenator;

    /**
     * Configuration for the used glue.
     */
    private final GlueConfig glueConfig;

    /**
     * Supplier for image sources.
     */
    private final ImageSourceSupplier imageSourceSupplier;

    public KnuthPlassTypeSettingConfig(
            Size pageSize,
            Insets pageInsets,
            double indentWidth,
            int looseness,
            double tolerance,
            double flaggedDemerit,
            double fitnessDemerit,
            FontDetailsSupplier fontDetailsSupplier,
            Hyphenator hyphenator,
            GlueConfig glueConfig,
            ImageSourceSupplier imageSourceSupplier
    ) {
        if (fontDetailsSupplier == null) {
            throw new NullPointerException("Cannot build line breaking configuration as the font details supplier is null which is required to properly typeset");
        }

        if (hyphenator == null) {
            throw new NullPointerException("Cannot build line breaking configuration as the hyphenator is null which is required");
        }

        if (glueConfig == null) {
            throw new NullPointerException("Cannot build line breaking configuration as the glue configuration is null which is required");
        }

        this.pageSize = pageSize;
        this.pageInsets = pageInsets;

        this.indentWidth = indentWidth;

        this.looseness = looseness;
        this.tolerance = tolerance;

        this.flaggedDemerit = flaggedDemerit;
        this.fitnessDemerit = fitnessDemerit;

        this.fontDetailsSupplier = fontDetailsSupplier;

        this.hyphenator = hyphenator;

        this.glueConfig = glueConfig;

        this.imageSourceSupplier = imageSourceSupplier;
    }

    /**
     * Get the size of the de.be.thaw.typeset.page to typeset on (in millimeters).
     *
     * @return de.be.thaw.typeset.page size
     */
    public Size getPageSize() {
        return pageSize;
    }

    /**
     * Get the supplier that is able to deliver details about a used font
     * needed to properly typeset a text.
     *
     * @return supplier
     */
    public FontDetailsSupplier getFontDetailsSupplier() {
        return fontDetailsSupplier;
    }

    /**
     * Get the hyphenator used to hyphenate words.
     *
     * @return hyphenator
     */
    public Hyphenator getHyphenator() {
        return hyphenator;
    }

    /**
     * Get the configuration for the used glue.
     *
     * @return glue config
     */
    public GlueConfig getGlueConfig() {
        return glueConfig;
    }

    /**
     * Tolerance of the badness.
     * This value is the maximum adjustment ratio allowed to fit a line.
     *
     * @return tolerance
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Get how many lines more than the optimum value are allowed.
     *
     * @return looseness
     */
    public int getLooseness() {
        return looseness;
    }

    /**
     * Get the demerit added when the break points of two consecutive lines
     * are flagged.
     *
     * @return flagged demerit
     */
    public double getFlaggedDemerit() {
        return flaggedDemerit;
    }

    /**
     * Get the demerit added when two consecutive lines have different
     * fitness classes.
     *
     * @return fitness demerit
     */
    public double getFitnessDemerit() {
        return fitnessDemerit;
    }

    /**
     * Get insets for the pages to typeset.
     *
     * @return insets
     */
    public Insets getPageInsets() {
        return pageInsets;
    }

    /**
     * Get the width of indentation.
     *
     * @return indentation width
     */
    public double getIndentWidth() {
        return indentWidth;
    }

    /**
     * Get the image source supplier.
     *
     * @return image source supplier
     */
    public ImageSourceSupplier getImageSourceSupplier() {
        return imageSourceSupplier;
    }

    /**
     * Create a new builder for the line breaking configuration.
     *
     * @return builder
     */
    public static KnuthPlassTypeSettingConfigBuilder newBuilder() {
        return new KnuthPlassTypeSettingConfigBuilder();
    }

}
