package de.be.thaw.typeset.kerning.glyph;

import de.be.thaw.typeset.util.Size;

import java.io.Serializable;
import java.util.List;

/**
 * Representation of a glyph of a font.
 */
public class Glyph implements Serializable {

    /**
     * ID of the glyph.
     */
    private final int glyphID;

    /**
     * Code point the glyph is representing.
     */
    private final int codePoint;

    /**
     * Size of the glyph.
     */
    private final Size size;

    /**
     * Position of the glyph.
     */
    private final Coordinate position;

    /**
     * Contours of the glyph.
     */
    private final List<List<Coordinate>> contours;

    public Glyph(int glyphID, int codePoint, Size size, Coordinate position, List<List<Coordinate>> contours) {
        this.glyphID = glyphID;
        this.codePoint = codePoint;
        this.size = size;
        this.position = position;
        this.contours = contours;
    }

    /**
     * Id of the glyph.
     */
    public int getGlyphID() {
        return glyphID;
    }

    /**
     * Get the code point of the glyph.
     *
     * @return code point
     */
    public int getCodePoint() {
        return codePoint;
    }

    /**
     * Get the size of the glyph.
     *
     * @return size
     */
    public Size getSize() {
        return size;
    }

    /**
     * Get the position of the glyph.
     *
     * @return position
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Get the glyphs contours.
     *
     * @return contours
     */
    public List<List<Coordinate>> getContours() {
        return contours;
    }

}
