package de.be.thaw.core.document;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.info.ThawInfo;
import de.be.thaw.reference.ReferenceModel;

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

    /**
     * Model managing references.
     */
    private final ReferenceModel referenceModel;

    public Document(ThawInfo info, DocumentNode root, ReferenceModel referenceModel) {
        this.info = info;
        this.root = root;
        this.referenceModel = referenceModel;
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

    /**
     * Get the model managing all document references.
     *
     * @return reference model
     */
    public ReferenceModel getReferenceModel() {
        return referenceModel;
    }

}
