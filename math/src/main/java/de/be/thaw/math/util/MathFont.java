package de.be.thaw.math.util;

import java.io.InputStream;

/**
 * Utility class providing access to math font(s).
 */
public class MathFont {

    /**
     * Location of the math font.
     */
    private static final String MATH_FONT_LOCATION = "/font/stix/STIX2Math.ttf";

    /**
     * Get the math font stream.
     *
     * @return math font stream
     */
    public static InputStream getMathFontStream() {
        return MathFont.class.getResourceAsStream(MATH_FONT_LOCATION);
    }

}
