package de.be.thaw.text.model.tree.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;

/**
 * Node representing a text box (paragraph, heading, image, ...).
 */
public class BoxNode extends Node {

    /**
     * Create new node.
     */
    public BoxNode() {
        super(NodeType.BOX);
    }

}
