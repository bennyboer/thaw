package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Root node of a MathML tree.
 */
public class RootNode extends MathMLNode {

    /**
     * The thickness of the root line.
     */
    private final double lineThickness;

    public RootNode(double lineThickness) {
        this("mroot", lineThickness);
    }

    public RootNode(String name, double lineThickness) {
        super(name);

        this.lineThickness = lineThickness;
    }

    public double getLineThickness() {
        return lineThickness;
    }

}
