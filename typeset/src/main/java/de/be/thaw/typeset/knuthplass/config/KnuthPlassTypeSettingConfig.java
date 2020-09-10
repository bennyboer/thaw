package de.be.thaw.typeset.knuthplass.config;

import de.be.thaw.font.ThawFont;
import de.be.thaw.text.parser.TextParser;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSourceSupplier;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Size;

import java.io.File;
import java.util.Properties;

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

    /**
     * Font to use for math expressions.
     */
    private final ThawFont mathFont;

    /**
     * The working directory.
     */
    private final File workingDirectory;

    /**
     * Text parser to use for parsing nested Thaw document text strings (for example captions).
     */
    private final TextParser textParser;

    /**
     * Offset to add to the page number.
     */
    private final int pageNumberOffset;

    /**
     * Properties used to fetch translations.
     */
    private final Properties properties;

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
            ImageSourceSupplier imageSourceSupplier,
            ThawFont mathFont,
            File workingDirectory,
            TextParser textParser,
            int pageNumberOffset,
            Properties properties
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

        if (workingDirectory == null) {
            throw new NullPointerException("Cannot build line breaking configuration as the working directory is null which is required");
        }

        if (textParser == null) {
            throw new NullPointerException("Cannot build line breaking configuration as a thaw document text parser needs to be specified (for example to parse nested captions for image paragraphs).");
        }

        if (properties == null) {
            throw new NullPointerException("Cannot build line breaking configuration as properties for translations are required");
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

        this.mathFont = mathFont;

        this.workingDirectory = workingDirectory;
        this.textParser = textParser;

        this.pageNumberOffset = pageNumberOffset;

        this.properties = properties;
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
     * Get the font to use for math expressions.
     *
     * @return font
     */
    public ThawFont getMathFont() {
        return mathFont;
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
     * Get the working directory.
     *
     * @return working directory
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Get the text parser to use to parse for example captions for image paragraphs.
     *
     * @return text parser
     */
    public TextParser getTextParser() {
        return textParser;
    }

    /**
     * Create a new builder for the line breaking configuration.
     *
     * @return builder
     */
    public static KnuthPlassTypeSettingConfigBuilder newBuilder() {
        return new KnuthPlassTypeSettingConfigBuilder();
    }

    /**
     * Get the offset added to the page number.
     *
     * @return page number offset
     */
    public int getPageNumberOffset() {
        return pageNumberOffset;
    }

    /**
     * Get the properties containing translations.
     *
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Create a new builder for the line breaking configuration.
     *
     * @param config to initialize settings from
     * @return builder
     */
    public static KnuthPlassTypeSettingConfigBuilder newBuilder(KnuthPlassTypeSettingConfig config) {
        return new KnuthPlassTypeSettingConfigBuilder()
                .setTextParser(config.getTextParser())
                .setWorkingDirectory(config.getWorkingDirectory())
                .setFitnessDemerit(config.getFitnessDemerit())
                .setFlaggedDemerit(config.getFlaggedDemerit())
                .setFontDetailsSupplier(config.getFontDetailsSupplier())
                .setGlueConfig(config.getGlueConfig())
                .setHyphenator(config.getHyphenator())
                .setImageSourceSupplier(config.getImageSourceSupplier())
                .setIndentWidth(config.getIndentWidth())
                .setLooseness(config.getLooseness())
                .setMathFont(config.getMathFont())
                .setPageInsets(config.getPageInsets())
                .setPageSize(config.getPageSize())
                .setTolerance(config.getTolerance())
                .setPageNumberOffset(config.getPageNumberOffset())
                .setProperties(config.getProperties());
    }

}
