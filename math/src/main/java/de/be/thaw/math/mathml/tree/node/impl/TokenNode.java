package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Node representing a token (identifier, text, ...).
 */
public abstract class TokenNode extends MathMLNode {

    /**
     * Text content of the node.
     */
    private final String text;

    /**
     * The math variant to use to display the text.
     */
    private final MathVariant mathVariant;

    /**
     * Factor by which the text is scaled.
     */
    private final double sizeFactor;

    public TokenNode(String name, String text, MathVariant mathVariant, double sizeFactor) {
        super(name);

        this.text = text;
        this.mathVariant = mathVariant;
        this.sizeFactor = sizeFactor;
    }

    /**
     * Get the text content of the node.
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

    /**
     * Get the factor by which the nodes text is scaled.
     *
     * @return size factor
     */
    public double getSizeFactor() {
        return sizeFactor;
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
