package de.be.thaw.font.util;

import de.be.thaw.util.Size;

/**
 * A size representation with the kerning adjustments included.
 */
public class KernedSize extends Size {

    /**
     * The adjustments per code point.
     */
    private final double[] kerningAdjustments;

    public KernedSize(double width, double height, double[] kerningAdjustments) {
        super(width, height);

        this.kerningAdjustments = kerningAdjustments;
    }

    public double[] getKerningAdjustments() {
        return kerningAdjustments;
    }

}
