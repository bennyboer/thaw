package de.be.thaw.text.model;

import de.be.thaw.text.model.tree.impl.RootNode;

/**
 * Text model representing a tree of the thaw document text format.
 */
public class TextModel {

    /**
     * Reference to the root node of the text model tree.
     */
    private final RootNode root;

    public TextModel(RootNode root) {
        this.root = root;
    }

    /**
     * Get the root node of the text model tree.
     *
     * @return root node
     */
    public RootNode getRoot() {
        return root;
    }

}
