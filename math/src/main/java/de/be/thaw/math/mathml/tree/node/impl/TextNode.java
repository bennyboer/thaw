package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Text <mtext> node.
 */
public class TextNode extends TokenNode {

    public TextNode(String text, MathVariant mathVariant, double sizeFactor) {
        super("mtext", text, mathVariant, sizeFactor);
    }

}
