package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Numeric <mn> node.
 */
public class NumericNode extends MathMLNode {

    /**
     * Stringified numeric value of the node.
     */
    private final String value;

    public NumericNode(String value) {
        super("mn");

        this.value = value;
    }

    /**
     * Get the numeric value of the node.
     *
     * @return numeric value
     */
    public String getValue() {
        return value;
    }

    @Override
    public void toString(int indent, StringBuilder builder) {
        builder.append(" ".repeat(indent));
        builder.append('-');
        builder.append(' ');
        builder.append(getName());
        builder.append(" [");
        builder.append(getValue());
        builder.append(']');
        builder.append('\n');

        for (var child : getChildren()) {
            child.toString(indent + 2, builder);
        }
    }

}
