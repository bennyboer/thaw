package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Identifier <mi> node.
 */
public class IdentifierNode extends MathMLNode {

    /**
     * Text content of the node (function name, variable, ...).
     */
    private final String text;

    /**
     * The math variant to use to display the text.
     */
    private final MathVariant mathVariant;

    public IdentifierNode(String text, MathVariant mathVariant) {
        super("mi");

        this.text = text;
        this.mathVariant = mathVariant;
    }

    /**
     * Get the text content of the node (function name, variable, ...).
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the math variant to use.
     *
     * @return math variant
     */
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
        builder.append(getText());
        builder.append(']');
        builder.append('\n');

        for (var child : getChildren()) {
            child.toString(indent + 2, builder);
        }
    }

}
