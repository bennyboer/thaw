package de.be.thaw.core.document.node;

import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.text.model.tree.Node;

import java.util.List;

/**
 * Node of the document.
 */
public class DocumentNode {

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
        this.node = node;
        this.style = style;
        this.children = children;
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
