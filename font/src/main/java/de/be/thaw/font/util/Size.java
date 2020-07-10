package de.be.thaw.font.util;

/**
 * Size representation.
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
