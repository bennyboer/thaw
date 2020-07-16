package de.be.thaw.core.document;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.util.PageRange;
import de.be.thaw.info.ThawInfo;
import de.be.thaw.reference.ReferenceModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    /**
     * Lookup of the document nodes by their ID.
     */
    private final Map<String, DocumentNode> nodeLookup = new HashMap<>();

    /**
     * Root node for headers (if any).
     */
    private final Map<PageRange, DocumentNode> headerNodes;

    /**
     * Root node for footers (if any).
     */
    private final Map<PageRange, DocumentNode> footerNodes;

    /**
     * List of foot notes in the document.
     * Mapping from source document node ID to the foot note document node.
     */
    private final Map<String, DocumentNode> footNotes;

    public Document(
            ThawInfo info,
            DocumentNode root,
            ReferenceModel referenceModel,
            Map<PageRange, DocumentNode> headerNodes,
            Map<PageRange, DocumentNode> footerNodes,
            Map<String, DocumentNode> footNotes
    ) {
        this.info = info;
        this.root = root;
        this.referenceModel = referenceModel;

        this.headerNodes = headerNodes;
        this.footerNodes = footerNodes;

        this.footNotes = footNotes;

        initLookup(root);
    }

    /**
     * Initialize the document node lookup for the passed node.
     *
     * @param node to initialize lookup for
     */
    private void initLookup(DocumentNode node) {
        nodeLookup.put(node.getId(), node);

        if (node.hasChildren()) {
            for (DocumentNode child : node.getChildren()) {
                initLookup(child);
            }
        }
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

    /**
     * Get a node by the passed ID.
     *
     * @param nodeID to get node for
     * @return the requested node (or an empty optional)
     */
    public Optional<DocumentNode> getNodeForId(String nodeID) {
        return Optional.ofNullable(nodeLookup.get(nodeID));
    }

    /**
     * Get the document nodes for all headers.
     *
     * @return header nodes
     */
    public Map<PageRange, DocumentNode> getHeaderNodes() {
        return headerNodes;
    }

    /**
     * Get the document nodes for all footers.
     *
     * @return footer nodes
     */
    public Map<PageRange, DocumentNode> getFooterNodes() {
        return footerNodes;
    }

    /**
     * Get the foot notes mapped by document node IDs.
     *
     * @return foot notes
     */
    public Map<String, DocumentNode> getFootNotes() {
        return footNotes;
    }

}
