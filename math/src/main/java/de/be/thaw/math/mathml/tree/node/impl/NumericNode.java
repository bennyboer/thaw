package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Numeric <mn> node.
 */
public class NumericNode extends MathMLNode {

    /**
     * Stringified numeric value of the node.
     */
    private final String value;

    /**
     * The math variant to use when displaying the value.
     */
    private final MathVariant mathVariant;

    public NumericNode(String value, MathVariant mathVariant) {
        super("mn");

        this.value = value;
        this.mathVariant = mathVariant;
    }

    /**
     * Get the numeric value of the node.
     *
     * @return numeric value
     */
    public String getValue() {
        return value;
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
        builder.append(getValue());
        builder.append(']');
        builder.append('\n');

        for (var child : getChildren()) {
            child.toString(indent + 2, builder);
        }
    }

}
