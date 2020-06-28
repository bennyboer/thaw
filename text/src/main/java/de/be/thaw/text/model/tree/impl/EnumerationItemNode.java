package de.be.thaw.text.model.tree.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.util.TextPosition;

/**
 * Node representing an item of an enumeration.
 */
public class EnumerationItemNode extends Node {

    /**
     * Position of the item in the original text.
     */
    private final TextPosition position;

    /**
     * Indent of the item.
     */
    private final int indent;

    public EnumerationItemNode(TextPosition position, int indent) {
        super(NodeType.ENUMERATION_ITEM);

        this.position = position;
        this.indent = indent;
    }

    @Override
    public TextPosition getTextPosition() {
        return position;
    }

    /**
     * Indent of the item.
     *
     * @return indent
     */
    public int getIndent() {
        return indent;
    }

}
