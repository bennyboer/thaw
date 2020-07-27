package de.be.thaw.math.mathml.tree.node;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Node of the MathMLTree.
 */
public abstract class MathMLNode {

    /**
     * Children of this node (if any).
     */
    private final List<MathMLNode> children = new ArrayList<>();

    /**
     * Name of the node.
     */
    private final String name;

    /**
     * The parent of the node.
     */
    @Nullable
    private MathMLNode parent;

    public MathMLNode(String name) {
        this.name = name;
    }

    /**
     * Get the children of this node.
     *
     * @return children
     */
    public List<MathMLNode> getChildren() {
        return children;
    }

    /**
     * Get the parent of the node.
     *
     * @return parent
     */
    public Optional<MathMLNode> getParent() {
        return Optional.ofNullable(parent);
    }

    /**
     * Add a child to the node.
     *
     * @param node to add child to
     */
    public void addChild(MathMLNode node) {
        children.add(node);

        node.parent = this;
    }

    /**
     * Get the name of the node.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * To String with indent.
     *
     * @param indent  of the node
     * @param builder to append string to
     */
    public void toString(int indent, StringBuilder builder) {
        builder.append(" ".repeat(indent));
        builder.append('-');
        builder.append(' ');
        builder.append(getName());
        builder.append('\n');

        for (var child : getChildren()) {
            child.toString(indent + 2, builder);
        }
    }

}
