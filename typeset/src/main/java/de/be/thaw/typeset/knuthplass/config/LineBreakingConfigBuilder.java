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
     * Default size of the de.be.thaw.typeset.page to typeset on (in millimeters).
     */
    private static final Size DEFAULT_PAGE_SIZE = new Size(210, 297);

    /**
     * Indent of the text in the first line.
     */
    private double firstLineIndent = DEFAULT_FIRST_LINE_INDENT;

    /**
     * Size of the de.be.thaw.typeset.page to typeset on.
     */
    private Size pageSize = DEFAULT_PAGE_SIZE;

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
     * Build the line breaking configuration.
     *
     * @return the build config
     */
    public LineBreakingConfig build() {
        return new LineBreakingConfig(
                pageSize,
                firstLineIndent,
                fontDetailsSupplier,
                hyphenator,
                glueConfig
        );
    }

}
