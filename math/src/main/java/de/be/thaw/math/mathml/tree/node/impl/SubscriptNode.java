package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Subscript <msub> node.
 */
public class SubscriptNode extends MathMLNode {

    /**
     * The shift by which we lower the sub script below the baseline.
     * A value of 0.5 means 50 % below the baseline.
     */
    private final double subscriptShift;

    public SubscriptNode(double subscriptShift) {
        super("msub");

        this.subscriptShift = subscriptShift;
    }

    /**
     * Get the subscript shift.
     *
     * @return subscript shift
     */
    public double getSubscriptShift() {
        return subscriptShift;
    }

}
