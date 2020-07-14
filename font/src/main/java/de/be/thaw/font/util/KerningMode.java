package de.be.thaw.font.util;

/**
 * Supported kerning modes.
 */
public enum KerningMode {

    /**
     * No kerning.
     */
    NONE,

    /**
     * Optical kerning using the glyph shape.
     */
    OPTICAL,

    /**
     * Native kerning using the kerning table included in the font files (if any).
     */
    NATIVE

}
