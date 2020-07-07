package de.be.thaw.core.document;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.info.ThawInfo;

/**
 * Representation of the thaw document.
 */
public class Document {

    /**
     * Info about the document.
     */
    private final ThawInfo info;

    /**
     * The root document node.
     */
    private final DocumentNode root;

    public Document(ThawInfo info, DocumentNode root) {
        this.info = info;
        this.root = root;
    }

    /**
     * Get info about the document.
     *
     * @return info
     */
    public ThawInfo getInfo() {
        return info;
    }

    /**
     * Get the root document node.
     *
     * @return root node
     */
    public DocumentNode getRoot() {
        return root;
    }

}
