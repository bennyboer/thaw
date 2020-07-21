package de.be.thaw.math.mathml.tree;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Tree representing a MathML tree.
 */
public class MathMLTree {

    /**
     * The MathML root node.
     */
    private final MathMLNode root;

    public MathMLTree(MathMLNode root) {
        this.root = root;
    }

    public MathMLNode getRoot() {
        return root;
    }

}
