package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.util.HorizontalAlignment;

/**
 * Node that is vertically aligning like mover, munder or munderover.
 */
public abstract class VerticalNode extends MathMLNode {

    /**
     * Alignment of the non-basis elements.
     */
    private final HorizontalAlignment alignment;

    public VerticalNode(String name, HorizontalAlignment alignment) {
        super(name);

        this.alignment = alignment;
    }

    /**
     * Alignment of the non-basis elements.
     *
     * @return alignment
     */
    public HorizontalAlignment getAlignment() {
        return alignment;
    }

}
