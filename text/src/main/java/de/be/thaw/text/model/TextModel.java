package de.be.thaw.text.model;

import de.be.thaw.text.parser.tree.Node;

/**
 * Text model representing a tree of the thaw document text format.
 */
public class TextModel {

    /**
     * Reference to the root node of the text model tree.
     */
    private final Node root;

    public TextModel(Node root) {
        this.root = root;
    }

    /**
     * Get the root node of the text model tree.
     *
     * @return root node
     */
    public Node getRoot() {
        return root;
    }

}
