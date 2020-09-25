package de.be.thaw.typeset.util;

/**
 * Insets of something.
 */
public class Insets {

    /**
     * Insets from top.
     */
    private final double top;

    /**
     * Insets from left.
     */
    private final double left;

    /**
     * Insets from bottom.
     */
    private final double bottom;

    /**
     * Insets from right.
     */
    private final double right;

    public Insets(double all) {
        this.top = all;
        this.left = all;
        this.bottom = all;
        this.right = all;
    }

    public Insets(double top, double right, double bottom, double left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    /**
     * Get the insets from top.
     *
     * @return top insets
     */
    public double getTop() {
        return top;
    }

    /**
     * Get the insets from left.
     *
     * @return left insets
     */
    public double getLeft() {
        return left;
    }

    /**
     * Get the insets from bottom.
     *
     * @return bottom insets
     */
    public double getBottom() {
        return bottom;
    }

    /**
     * Get the insets from right.
     *
     * @return right insets
     */
    public double getRight() {
        return right;
    }

}
