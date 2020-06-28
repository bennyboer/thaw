package de.be.thaw.text.model.tree;

import de.be.thaw.text.util.TextPosition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Node of the thaw document text format tree.
 */
public abstract class Node {

    /**
     * Type of the node.
     */
    private final NodeType type;

    /**
     * List of children.
     */
    private List<Node> children;

    /**
     * Parent of the node.
     */
    private Node parent;

    /**
     * Create new node.
     *
     * @param type of the node
     */
    public Node(NodeType type) {
        this.type = type;
    }

    /**
     * Whether this node is a root node.
     *
     * @return whether root node
     */
    public boolean isRoot() {
        return this.parent == null;
    }

    /**
     * Check whether the node is a leaf.
     *
     * @return whether this node is a leaf
     */
    public boolean isLeaf() {
        return !this.hasChildren();
    }

    /**
     * Get all children.
     *
     * @return children
     */
    public List<Node> children() {
        return children;
    }

    /**
     * Check whether the node has children.
     *
     * @return whether the node has children
     */
    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    /**
     * Add a child to this node.
     *
     * @param node to add as child
     */
    public void addChild(Node node) {
        if (children == null) {
            children = new ArrayList<>();
        }

        children.add(node);
        node.parent = this;
    }

    /**
     * Remove a child from this node.
     *
     * @param node to remove
     * @return whether the node could be removed
     */
    public boolean removeChild(Node node) {
        if (children == null) {
            return false;
        }

        return children.remove(node);
    }

    /**
     * Get the nodes type.
     *
     * @return type
     */
    public NodeType getType() {
        return type;
    }

    /**
     * Get the nodes parent.
     *
     * @return parent
     */
    @Nullable
    public Node getParent() {
        return parent;
    }

    /**
     * Get the text position for the node.
     *
     * @return text position
     */
    public TextPosition getTextPosition() {
        if (hasChildren()) {
            // Create new text position object over all children tokens
            if (children().size() == 1) {
                return children.get(0).getTextPosition();
            } else {
                return new TextPosition(
                        children().get(0).getTextPosition().getStartLine(),
                        children().get(0).getTextPosition().getStartPos(),
                        children().get(children().size() - 1).getTextPosition().getEndLine(),
                        children().get(children().size() - 1).getTextPosition().getEndPos()
                );
            }
        } else {
            return null;
        }
    }

    /**
     * Get string that represents the nodes internal settings (e. g. Formatting, ...).
     *
     * @return string representation
     */
    public String getInternalNodeRepresentation() {
        return "~";
    }

    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * Node to string.
     *
     * @param indent to prepend
     * @return the stringified node
     */
    private String toString(int indent) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(String.format(
                "%s- [%s]: %s\n",
                " ".repeat(indent),
                type.name(),
                getInternalNodeRepresentation()
                )
        );

        if (hasChildren()) {
            for (Node child : children()) {
                buffer.append(child.toString(indent + 2));
            }
        }

        return buffer.toString();
    }

}
