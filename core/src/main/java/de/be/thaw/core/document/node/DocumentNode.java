package de.be.thaw.core.document.node;

import de.be.thaw.style.model.style.Styles;
import de.be.thaw.text.model.tree.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Node of the document.
 */
public class DocumentNode {

    /**
     * ID of the node.
     */
    private final String id;

    /**
     * The underlying text model node.
     */
    private final Node node;

    /**
     * Styles applied to the node.
     */
    private final Styles styles;

    /**
     * Children of the node.
     */
    private final List<DocumentNode> children = new ArrayList<>();

    /**
     * The parent node.
     */
    private final DocumentNode parent;

    public DocumentNode(Node node, DocumentNode parent, Styles styles) {
        this(UUID.randomUUID().toString(), node, parent, styles);
    }

    public DocumentNode(String id, Node node, DocumentNode parent, Styles styles) {
        this.id = id;
        this.node = node;
        this.styles = styles;
        this.parent = parent;

        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    /**
     * Get the ID of the node.
     *
     * @return ID
     */
    public String getId() {
        return id;
    }

    public Node getTextNode() {
        return node;
    }

    public Styles getStyles() {
        return styles;
    }

    public List<DocumentNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public DocumentNode getParent() {
        return parent;
    }

}
