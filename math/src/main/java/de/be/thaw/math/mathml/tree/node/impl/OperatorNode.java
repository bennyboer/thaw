package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Operator <mo> node.
 */
public class OperatorNode extends TokenNode {

    /**
     * Width of a space to the left of an arithmetic operator (if any).
     */
    private final double leftSpaceWidth;

    /**
     * Width of a space to the right of an arithmetic operator (if any).
     */
    private final double rightSpaceWidth;

    public OperatorNode(String text, MathVariant mathVariant, double sizeFactor, double leftSpaceWidth, double rightSpaceWidth) {
        super("mo", text, mathVariant, sizeFactor);

        this.leftSpaceWidth = leftSpaceWidth;
        this.rightSpaceWidth = rightSpaceWidth;
    }

    public double getLeftSpaceWidth() {
        return leftSpaceWidth;
    }

    public double getRightSpaceWidth() {
        return rightSpaceWidth;
    }

}
