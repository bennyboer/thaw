package de.be.thaw.core.document.node;

import de.be.thaw.core.document.node.style.DocumentNodeStyle;
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
     * Style of the node.
     */
    private final DocumentNodeStyle style;

    /**
     * Children of the node.
     */
    private final List<DocumentNode> children = new ArrayList<>();

    /**
     * The parent node.
     */
    private final DocumentNode parent;

    public DocumentNode(Node node, DocumentNode parent, DocumentNodeStyle style) {
        this(UUID.randomUUID().toString(), node, parent, style);
    }

    public DocumentNode(String id, Node node, DocumentNode parent, DocumentNodeStyle style) {
        this.id = id;
        this.node = node;
        this.style = style;
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

    public DocumentNodeStyle getStyle() {
        return style;
    }

    public List<DocumentNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public DocumentNode getParent() {
        return parent;
    }

}
