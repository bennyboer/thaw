package de.be.thaw.text.model.tree.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.util.TextPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Node representing a thingy.
 */
public class ThingyNode extends Node {

    /**
     * Name of the thingy.
     */
    private final String name;

    /**
     * Arguments of the thingy.
     */
    private final Collection<String> arguments;

    /**
     * Options of the thingy.
     */
    private final Map<String, String> options;

    /**
     * The position of the thingy in the original text.
     */
    private final TextPosition position;

    public ThingyNode(String name, Collection<String> arguments, Map<String, String> options, TextPosition position) {
        super(NodeType.THINGY);

        this.name = name;
        this.arguments = arguments;
        this.options = options;

        this.position = position;
    }

    @Override
    public @Nullable TextPosition getTextPosition() {
        return position;
    }

    @Override
    public String getInternalNodeRepresentation() {
        return String.format(
                "Name: '%s', Args: %s, Options: %s",
                getName(),
                getArguments(),
                getOptions()
        );
    }

    /**
     * Get the name of the thingy.
     *
     * @return thingy name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the thingys arguments.
     *
     * @return arguments
     */
    public Collection<String> getArguments() {
        return arguments;
    }

    /**
     * Get the thingys options.
     *
     * @return options
     */
    public Map<String, String> getOptions() {
        return options;
    }

}
