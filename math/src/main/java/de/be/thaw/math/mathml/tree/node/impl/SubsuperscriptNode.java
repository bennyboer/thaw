package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Subsupscript <msubsup> node.
 */
public class SubsuperscriptNode extends MathMLNode {

    /**
     * The shift by which we lower the sub script below the baseline.
     * A value of 0.5 means 50 % below the baseline.
     */
    private final double subscriptShift;

    /**
     * The shift by which we raise the super script above the baseline.
     * A value of 0.5 means 50 % above the baseline.
     */
    private final double superscriptShift;

    public SubsuperscriptNode(double subscriptShift, double superscriptShift) {
        super("msubsup");

        this.subscriptShift = subscriptShift;
        this.superscriptShift = superscriptShift;
    }

    /**
     * Get the subscript shift.
     *
     * @return subscript shift
     */
    public double getSubscriptShift() {
        return subscriptShift;
    }

    /**
     * Get the superscript shift.
     *
     * @return superscript shift
     */
    public double getSuperscriptShift() {
        return superscriptShift;
    }

}
