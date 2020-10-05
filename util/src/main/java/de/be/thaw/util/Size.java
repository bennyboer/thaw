package de.be.thaw.util;

import java.io.Serializable;

/**
 * Representation of a 2-dimensional objects size (height and width).
 */
public class Size implements Serializable {

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

    /**
     * Get the width.
     *
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get the height.
     *
     * @return height
     */
    public double getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Size size = (Size) o;

        if (Double.compare(size.width, width) != 0) return false;
        return Double.compare(size.height, height) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(width);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("%f x %f", getWidth(), getHeight());
    }

}
