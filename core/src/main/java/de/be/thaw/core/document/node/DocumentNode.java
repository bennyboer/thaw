package de.be.thaw.core.document.node;

import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.text.model.tree.Node;

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
    private final List<DocumentNode> children;

    public DocumentNode(Node node, DocumentNodeStyle style, List<DocumentNode> children) {
        this(UUID.randomUUID().toString(), node, style, children);
    }

    public DocumentNode(String id, Node node, DocumentNodeStyle style, List<DocumentNode> children) {
        this.id = id;
        this.node = node;
        this.style = style;
        this.children = children;
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
        return children != null && children.size() > 0;
    }

}
