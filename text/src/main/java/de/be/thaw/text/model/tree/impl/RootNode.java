package de.be.thaw.text.model.tree.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;

/**
 * The root node of the thaw document text format tree.
 */
public class RootNode extends Node {

    /**
     * Create new node.
     */
    public RootNode() {
        super(NodeType.ROOT);
    }

}
