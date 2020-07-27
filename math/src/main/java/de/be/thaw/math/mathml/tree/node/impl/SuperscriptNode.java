package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Superscript <msup> node.
 */
public class SuperscriptNode extends MathMLNode {

    /**
     * The shift by which we raise the super script above the baseline.
     * A value of 0.5 means 50 % above the baseline.
     */
    private final double superscriptShift;

    public SuperscriptNode(double superscriptShift) {
        super("msup");

        this.superscriptShift = superscriptShift;
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
