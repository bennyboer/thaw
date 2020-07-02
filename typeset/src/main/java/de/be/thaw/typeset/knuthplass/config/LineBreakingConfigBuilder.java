package de.be.thaw.typeset.knuthplass.config;

import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.util.Size;

/**
 * Builder for the line breaking configuration.
 */
public class LineBreakingConfigBuilder {

    /**
     * The default first line indent.
     */
    private static final double DEFAULT_FIRST_LINE_INDENT = 0;

    /**
     * Default size of the de.be.thaw.typeset.page to typeset on (in arbitrary units).
     */
    private static final Size DEFAULT_PAGE_SIZE = new Size(210, 297);

    /**
     * The default badness tolerance.
     * This means the maximum adjustment ratio allowed to fit a line.
     */
    private static final double DEFAULT_TOLERANCE = 1;

    /**
     * The default looseness setting.
     */
    private static final int DEFAULT_LOOSENESS = 0;

    /**
     * Default demerit added when the break points of two consecutive lines
     * are flagged.
     */
    private static final double DEFAULT_FLAGGED_DEMERIT = 100;

    /**
     * Default demerit added when two consecutive lines have different
     * fitness classes.
     */
    private static final double DEFAULT_FITNESS_DEMERIT = 100;

    /**
     * The default line height.
     */
    private static final double DEFAULT_LINE_HEIGHT = 10;

    /**
     * Indent of the text in the first line.
     */
    private double firstLineIndent = DEFAULT_FIRST_LINE_INDENT;

    /**
     * The height of a line.
     */
    private double lineHeight = DEFAULT_LINE_HEIGHT;

    /**
     * Size of the de.be.thaw.typeset.page to typeset on.
     */
    private Size pageSize = DEFAULT_PAGE_SIZE;

    /**
     * Tolerance of the badness.
     * This value is the maximum adjustment ratio allowed to fit a line.
     */
    private double tolerance = DEFAULT_TOLERANCE;

    /**
     * Defines how many lines more than the optimum value are allowed.
     */
    private int looseness = DEFAULT_LOOSENESS;

    /**
     * Demerit added when the break points of two consecutive lines
     * are flagged.
     */
    private double flaggedDemerit = DEFAULT_FLAGGED_DEMERIT;

    /**
     * Demerit added when two consecutive lines have different
     * fitness classes.
     */
    private double fitnessDemerit = DEFAULT_FITNESS_DEMERIT;

    /**
     * Supplier for font details.
     */
    private FontDetailsSupplier fontDetailsSupplier;

    /**
     * Hyphenator used to hyphenate words.
     */
    private Hyphenator hyphenator;

    /**
     * Configuration for the used glue.
     */
    private GlueConfig glueConfig;

    /**
     * Get the height of a line.
     *
     * @return line height
     */
    public double getLineHeight() {
        return lineHeight;
    }

    /**
     * Set the height of a line.
     *
     * @param lineHeight to set
     */
    public LineBreakingConfigBuilder setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;

        return this;
    }

    /**
     * Get the indent of the first line of a paragraph.
     *
     * @return indent
     */
    public double getFirstLineIndent() {
        return firstLineIndent;
    }

    /**
     * Set the indent of the first line of a paragraph.
     *
     * @param firstLineIndent to set
     */
    public LineBreakingConfigBuilder setFirstLineIndent(double firstLineIndent) {
        this.firstLineIndent = firstLineIndent;

        return this;
    }

    /**
     * Get the size of the de.be.thaw.typeset.page to typeset on (in mm).
     *
     * @return size of the de.be.thaw.typeset.page
     */
    public Size getPageSize() {
        return pageSize;
    }

    /**
     * Set the de.be.thaw.typeset.page size of the de.be.thaw.typeset.page to typeset on (in mm).
     *
     * @param pageSize to set
     */
    public LineBreakingConfigBuilder setPageSize(Size pageSize) {
        this.pageSize = pageSize;

        return this;
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
     * Set the font details supplier needed to request details about a used font.
     *
     * @param fontDetailsSupplier to set
     */
    public LineBreakingConfigBuilder setFontDetailsSupplier(FontDetailsSupplier fontDetailsSupplier) {
        this.fontDetailsSupplier = fontDetailsSupplier;

        return this;
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
     * Set the hyphenator used to hyphenate words.
     *
     * @param hyphenator to set
     */
    public LineBreakingConfigBuilder setHyphenator(Hyphenator hyphenator) {
        this.hyphenator = hyphenator;

        return this;
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
     * Set the configuration for the used glue.
     *
     * @param glueConfig to set
     */
    public LineBreakingConfigBuilder setGlueConfig(GlueConfig glueConfig) {
        this.glueConfig = glueConfig;

        return this;
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
     * Set the tolerance of the badness.
     * This value is the maximum adjustment ratio allowed to fit a line.
     *
     * @param tolerance to set
     */
    public LineBreakingConfigBuilder setTolerance(double tolerance) {
        this.tolerance = tolerance;

        return this;
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
     * Set how many lines more than the optimum value are allowed.
     *
     * @param looseness to set
     */
    public LineBreakingConfigBuilder setLooseness(int looseness) {
        this.looseness = looseness;

        return this;
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
     * Set the demerit added when break points of two consecutive lines
     * are flagged.
     *
     * @param flaggedDemerit to set
     */
    public LineBreakingConfigBuilder setFlaggedDemerit(double flaggedDemerit) {
        this.flaggedDemerit = flaggedDemerit;

        return this;
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
     * Set the demerit added when two consecutive lines have different
     * fitness classes.
     *
     * @param fitnessDemerit to set
     */
    public LineBreakingConfigBuilder setFitnessDemerit(double fitnessDemerit) {
        this.fitnessDemerit = fitnessDemerit;

        return this;
    }

    /**
     * Build the line breaking configuration.
     *
     * @return the build config
     */
    public LineBreakingConfig build() {
        return new LineBreakingConfig(
                pageSize,
                lineHeight,
                firstLineIndent,
                looseness,
                tolerance,
                flaggedDemerit,
                fitnessDemerit,
                fontDetailsSupplier,
                hyphenator,
                glueConfig
        );
    }

}