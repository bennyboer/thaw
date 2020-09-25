package de.be.thaw.util.color;

/**
 * A color value expressed in RGB.
 */
public class Color {

    /**
     * Red part of the color.
     */
    private final double red;

    /**
     * Green part of the color.
     */
    private final double green;

    /**
     * Blue part of the color.
     */
    private final double blue;

    /**
     * Alpha of the color.
     */
    private final double alpha;

    public Color(double red, double green, double blue) {
        this(red, green, blue, 1.0);
    }

    public Color(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * Get the red part of the color.
     *
     * @return red
     */
    public double getRed() {
        return red;
    }

    /**
     * Get the green part of the color.
     *
     * @return green
     */
    public double getGreen() {
        return green;
    }

    /**
     * Get the blue part of the color.
     *
     * @return blue
     */
    public double getBlue() {
        return blue;
    }

    /**
     * Get the alpha part of the color.
     *
     * @return alpha
     */
    public double getAlpha() {
        return alpha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (Double.compare(color.red, red) != 0) return false;
        if (Double.compare(color.green, green) != 0) return false;
        if (Double.compare(color.blue, blue) != 0) return false;
        return Double.compare(color.alpha, alpha) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(red);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(green);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(blue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(alpha);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("rgba(%f, %f, %f, %f)", getRed(), getGreen(), getBlue(), getAlpha());
    }

}
