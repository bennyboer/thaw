package de.be.thaw.text.parser.tree;

import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.util.TextPosition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Node {

    /**
     * Type of the node.
     */
    private final NodeType type;

    /**
     * Token the node is representing.
     */
    @Nullable
    private final Token token;

    /**
     * List of children.
     */
    private List<Node> children;

    /**
     * Parent of the node.
     */
    private Node parent;

    public Node(NodeType type, @Nullable Token token) {
        this.type = type;
        this.token = token;
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
     * Get the token the node is representing.
     *
     * @return token
     */
    @Nullable
    public Token getToken() {
        return token;
    }

    /**
     * Check whether the nodes allows children.
     *
     * @return whether allows children
     */
    public boolean allowsChildren() {
        return type == NodeType.ROOT
                || type == NodeType.BOX
                || type == NodeType.FORMATTED
                || type == NodeType.ENUMERATION
                || type == NodeType.ENUMERATION_ITEM;
    }

    /**
     * Get the text position for the node.
     * May return null if the node does not have a token and children.
     *
     * @return text position
     */
    @Nullable
    public TextPosition getTextPosition() {
        if (token != null) {
            return token.getPosition();
        } else if (hasChildren()) {
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
        }

        return null;
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
                token != null ? token.toString() : 'X')
        );

        if (hasChildren()) {
            for (Node child : children()) {
                buffer.append(child.toString(indent + 2));
            }
        }

        return buffer.toString();
    }

}
