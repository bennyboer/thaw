package de.be.thaw.typeset.util;

/**
 * Object representing a position on a 2-dimensional plane.
 */
public class Position {

    /**
     * The x-offset from the zero point.
     */
    private final double x;

    /**
     * The y-offset from the zero point.
     */
    private final double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x-offset.
     *
     * @return x-offset
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y-offset.
     *
     * @return y-offset
     */
    public double getY() {
        return y;
    }

}
