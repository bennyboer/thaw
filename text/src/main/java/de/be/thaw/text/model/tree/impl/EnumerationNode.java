package de.be.thaw.text.model.tree.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;

/**
 * Node representing an enumeration.
 */
public class EnumerationNode extends Node {

    /**
     * Level of the enumeration.
     * Starting at 1.
     */
    private final int level;

    public EnumerationNode(int level) {
        super(NodeType.ENUMERATION);

        this.level = level;
    }

    @Override
    public String getInternalNodeRepresentation() {
        return String.format(
                "Level: %d",
                getLevel()
        );
    }

    /**
     * Get the enumeration level.
     *
     * @return level
     */
    public int getLevel() {
        return level;
    }

}
