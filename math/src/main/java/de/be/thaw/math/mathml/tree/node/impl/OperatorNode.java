package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Operator <mo> node.
 */
public class OperatorNode extends MathMLNode {

    /**
     * The operator in string form.
     */
    private final String operator;

    public OperatorNode(String operator) {
        super("mo");

        this.operator = operator;
    }

    /**
     * Get the operator.
     *
     * @return operator
     */
    public String getOperator() {
        return operator;
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
