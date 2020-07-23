package de.be.thaw.math.mathml.typeset.config;

import de.be.thaw.font.ThawFont;

/**
 * Configuration for the math typesetting.
 */
public class MathTypesetConfig {

    /**
     * Font to use during typesetting.
     */
    private final ThawFont font;

    /**
     * Font size to use during typesetting.
     */
    private final double fontSize;

    public MathTypesetConfig(ThawFont font, double fontSize) {
        this.font = font;
        this.fontSize = fontSize;
    }

    /**
     * Get the font to use during typesetting.
     *
     * @return font
     */
    public ThawFont getFont() {
        return font;
    }

    /**
     * Get the font size to use during typesetting.
     *
     * @return font size
     */
    public double getFontSize() {
        return fontSize;
    }

}
