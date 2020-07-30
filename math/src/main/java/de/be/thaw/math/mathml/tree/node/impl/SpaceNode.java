package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Space <mspace> node.
 */
public class SpaceNode extends MathMLNode {

    /**
     * Width of the space node.
     */
    private final double width;

    /**
     * Height of the space node (baseline).
     */
    private final double height;

    /**
     * Depth of the space node (under the baseline).
     */
    private final double depth;

    public SpaceNode(double width, double height, double depth) {
        super("mspace");
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    /**
     * Get the width.
     *
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get the height (baseline).
     *
     * @return height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Get the depth (height under baseline).
     *
     * @return depth
     */
    public double getDepth() {
        return depth;
    }

}
