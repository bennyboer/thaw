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

    public OperatorNode(String operator, MathVariant mathVariant) {
        super("mo");

        this.operator = operator;
        this.mathVariant = mathVariant;
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
