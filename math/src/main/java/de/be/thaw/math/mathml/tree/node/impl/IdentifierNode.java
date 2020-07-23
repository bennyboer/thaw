package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Identifier <mi> node.
 */
public class IdentifierNode extends MathMLNode {

    /**
     * Text content of the node (function name, variable, ...).
     */
    private final String text;

    public IdentifierNode(String text) {
        super("mi");

        this.text = text;
    }

    /**
     * Get the text content of the node (function name, variable, ...).
     *
     * @return text
     */
    public String getText() {
        return text;
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
