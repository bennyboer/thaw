package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.util.HorizontalAlignment;

/**
 * Fraction <mfrac> node.
 */
public class FractionNode extends MathMLNode {

    /**
     * Whether the fraction line should bevelled (diagonal line = true)
     * or normal (horizontally = false).
     */
    private final boolean bevelled;

    /**
     * The alignment of the numerator.
     */
    private final HorizontalAlignment numeratorAlignment;

    /**
     * The alignment of the denominator.
     */
    private final HorizontalAlignment denominatorAlignment;

    /**
     * The thickness of the fraction line.
     */
    private final double lineThickness;

    public FractionNode(boolean bevelled, HorizontalAlignment numeratorAlignment, HorizontalAlignment denominatorAlignment, double lineThickness) {
        super("mfrac");

        this.bevelled = bevelled;
        this.numeratorAlignment = numeratorAlignment;
        this.denominatorAlignment = denominatorAlignment;
        this.lineThickness = lineThickness;
    }

    /**
     * Check whether the fraction line should be bevelled (diagonal) or normal (horizontally).
     *
     * @return bevelled
     */
    public boolean isBevelled() {
        return bevelled;
    }

    /**
     * Get the alignment of the numerator.
     *
     * @return alignment
     */
    public HorizontalAlignment getNumeratorAlignment() {
        return numeratorAlignment;
    }

    /**
     * Get the alignment of the denominator.
     *
     * @return alignment
     */
    public HorizontalAlignment getDenominatorAlignment() {
        return denominatorAlignment;
    }

    /**
     * Get the thickness of the fraction line.
     *
     * @return thickness
     */
    public double getLineThickness() {
        return lineThickness;
    }

}
