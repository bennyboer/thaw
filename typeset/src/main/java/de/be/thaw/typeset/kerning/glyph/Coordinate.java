package de.be.thaw.typeset.kerning.glyph;

import java.io.Serializable;

/**
 * A coordinate of a Glyph contour point.
 */
public class Coordinate implements Serializable {

    /**
     * The x coordinate.
     */
    private final double x;

    /**
     * The y coordinate.
     */
    private final double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
