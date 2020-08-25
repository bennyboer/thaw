package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Identifier <mi> node.
 */
public class IdentifierNode extends TokenNode {

    public IdentifierNode(String text, MathVariant mathVariant, double sizeFactor) {
        super("mi", text, mathVariant, sizeFactor);
    }

}
