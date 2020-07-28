package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathVariant;

/**
 * Numeric <mn> node.
 */
public class NumericNode extends TokenNode {

    public NumericNode(String text, MathVariant mathVariant, double sizeFactor) {
        super("mn", text, mathVariant, sizeFactor);
    }

}
