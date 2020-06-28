package de.be.thaw.text.model.tree.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.util.TextPosition;

/**
 * Node representing unformatted, plain text.
 */
public class TextNode extends Node {

    /**
     * The value of the node.
     */
    private final String value;

    /**
     * Position of the text in the original text file.
     */
    private final TextPosition position;

    public TextNode(String value, TextPosition position) {
        super(NodeType.TEXT);

        this.value = value;
        this.position = position;
    }

    @Override
    public TextPosition getTextPosition() {
        return position;
    }

    @Override
    public String getInternalNodeRepresentation() {
        return String.format("'%s'", getValue());
    }

    /**
     * Get the nodes value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

}
