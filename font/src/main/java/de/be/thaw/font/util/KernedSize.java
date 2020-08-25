package de.be.thaw.font.util;

/**
 * A size representation with the kerning adjustments included.
 */
public class KernedSize extends StringSize {

    /**
     * The adjustments per code point.
     */
    private final double[] kerningAdjustments;

    public KernedSize(double width, double height, double ascent, double descent, double[] kerningAdjustments) {
        super(width, height, ascent, descent);

        this.kerningAdjustments = kerningAdjustments;
    }

    public double[] getKerningAdjustments() {
        return kerningAdjustments;
    }

}
