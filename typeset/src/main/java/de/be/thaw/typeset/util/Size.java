package de.be.thaw.typeset.util;

/**
 * Representation of a 2-dimensional objects size (height and width).
 */
public class Size {

    /**
     * Width.
     */
    private final double width;

    /**
     * Height.
     */
    private final double height;

    public Size(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}
