package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Operator <mo> node.
 */
public class OperatorNode extends MathMLNode {

    /**
     * The operator in string form.
     */
    private final String operator;

    /**
     * The math variant to display the operator with.
     */
    private final MathVariant mathVariant;

    /**
     * Width of a space to the left (if any).
     */
    private final double leftSpaceWidth;

    /**
     * Width of a space to the right (if any).
     */
    private final double rightSpaceWidth;

    public OperatorNode(String operator, MathVariant mathVariant, double leftSpaceWidth, double rightSpaceWidth) {
        super("mo");

        this.operator = operator;
        this.mathVariant = mathVariant;
        this.leftSpaceWidth = leftSpaceWidth;
        this.rightSpaceWidth = rightSpaceWidth;
    }

    /**
     * Get the operator.
     *
     * @return operator
     */
    public String getOperator() {
        return operator;
    }

    public MathVariant getMathVariant() {
        return mathVariant;
    }

    public double getLeftSpaceWidth() {
        return leftSpaceWidth;
    }

    public double getRightSpaceWidth() {
        return rightSpaceWidth;
    }

    @Override
    public void toString(int indent, StringBuilder builder) {
        builder.append(" ".repeat(indent));
        builder.append('-');
        builder.append(' ');
        builder.append(getName());
        builder.append(" [");
        builder.append(getOperator());
        builder.append(']');
        builder.append('\n');

        for (var child : getChildren()) {
            child.toString(indent + 2, builder);
        }
    }

}
