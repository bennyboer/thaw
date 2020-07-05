package de.be.thaw.typeset.knuthplass.config;

import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.typeset.util.Size;

/**
 * Configuration of the Knuth-Plass line breaking algorithm.
 */
public class KnuthPlassTypeSettingConfig {

    /**
     * Indent of the text in the first line or a paragraph.
     */
    private final double firstLineIndent;

    /**
     * Size of the de.be.thaw.typeset.page to typeset on (in arbitrary units).
     */
    private final Size pageSize;

    /**
     * Insets of the pages.
     */
    private final Insets pageInsets;

    /**
     * Height of a line.
     */
    private final double lineHeight;

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

    public KnuthPlassTypeSettingConfig(
            Size pageSize,
            Insets pageInsets,
            double lineHeight,
            double firstLineIndent,
            int looseness,
            double tolerance,
            double flaggedDemerit,
            double fitnessDemerit,
            FontDetailsSupplier fontDetailsSupplier,
            Hyphenator hyphenator,
            GlueConfig glueConfig
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
        this.firstLineIndent = firstLineIndent;
        this.lineHeight = lineHeight;

        this.looseness = looseness;
        this.tolerance = tolerance;

        this.flaggedDemerit = flaggedDemerit;
        this.fitnessDemerit = fitnessDemerit;

        this.fontDetailsSupplier = fontDetailsSupplier;

        this.hyphenator = hyphenator;

        this.glueConfig = glueConfig;
    }

    /**
     * Get the height of a line.
     *
     * @return line height
     */
    public double getLineHeight() {
        return lineHeight;
    }

    /**
     * Get the first line indentation of a paragraph.
     *
     * @return first line indentation
     */
    public double getFirstLineIndent() {
        return firstLineIndent;
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
     * Create a new builder for the line breaking configuration.
     *
     * @return builder
     */
    public static KnuthPlassTypeSettingConfigBuilder newBuilder() {
        return new KnuthPlassTypeSettingConfigBuilder();
    }

}
