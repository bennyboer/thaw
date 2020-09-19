package de.be.thaw.style.model.style.value;

/**
 * A color value that a style property like "color", "background", .. can have.
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

}
