package de.be.thaw.math.mathml.tree.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node of the MathMLTree.
 */
public abstract class MathMLNode {

    /**
     * Attributes of the node.
     */
    private final Map<String, String> attributes = new HashMap<>();

    /**
     * Children of this node (if any).
     */
    private final List<MathMLNode> children = new ArrayList<>();

    /**
     * Name of the node.
     */
    private final String name;

    public MathMLNode(String name) {
        this.name = name;
    }

    /**
     * Get the attributes on this node.
     *
     * @return attributes
     */
    public Map<String, String> getAttributes() {
        return attributes;
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
