package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.util.HorizontalAlignment;

/**
 * Under and over <munder> node.
 */
public class UnderOverNode extends VerticalNode {

    public UnderOverNode(HorizontalAlignment alignment) {
        super("munderover", alignment);
    }

}
