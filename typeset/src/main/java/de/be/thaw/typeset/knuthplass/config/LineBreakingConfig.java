package de.be.thaw.typeset.knuthplass.config;

import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.util.Size;

/**
 * Configuration of the Knuth-Plass line breaking algorithm.
 */
public class LineBreakingConfig {

    /**
     * Indent of the text in the first line or a paragraph.
     */
    private final double firstLineIndent;

    /**
     * Size of the de.be.thaw.typeset.page to typeset on (in millimeters).
     */
    private final Size pageSize;

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

    public LineBreakingConfig(
            Size pageSize,
            double firstLineIndent,
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
        this.firstLineIndent = firstLineIndent;

        this.fontDetailsSupplier = fontDetailsSupplier;

        this.hyphenator = hyphenator;

        this.glueConfig = glueConfig;
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
     * Create a new builder for the line breaking configuration.
     *
     * @return builder
     */
    public static LineBreakingConfigBuilder newBuilder() {
        return new LineBreakingConfigBuilder();
    }

}
