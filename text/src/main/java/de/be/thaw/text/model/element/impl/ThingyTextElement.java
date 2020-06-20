package de.be.thaw.text.model.element.impl;

import de.be.thaw.text.model.element.TextElement;
import de.be.thaw.text.model.element.TextElementType;

import java.util.Collection;
import java.util.Map;

/**
 * Text element that represents a thingy.
 * <p>
 * For example #H1, label="test"# is a thingy.
 * It starts and ends with a #-character.
 * The first value H1 is the name of the thingy.
 * It may have an indefinite amount of arguments and options afterwards, separated by commas.
 * <p>
 * Arguments example: #NAME, test, "dwefh fweg"# -> test and "dwefh fweg are arguments.
 * Arguments must occur after the thingy name.
 * <p>
 * Options example: #NAME, label="test", boolopt, key=value#
 * Options are key-value pairs or special boolean options like 'boolopt' that are shortcuts for 'boolopt=true'.
 * They must be specified after arguments.
 */
public class ThingyTextElement implements TextElement {

    /**
     * Name of the thingy.
     */
    private final String name;

    /**
     * Arguments of the thingy.
     */
    private final Collection<String> arguments;

    /**
     * Options key-value pairs mapping.
     */
    private final Map<String, String> options;

    public ThingyTextElement(String name, Collection<String> arguments, Map<String, String> options) {
        this.name = name;
        this.arguments = arguments;
        this.options = options;
    }

    @Override
    public TextElementType getType() {
        return TextElementType.THINGY;
    }

    /**
     * Get the name of the thingy.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the thingy text elements arguments
     *
     * @return arguments
     */
    public Collection<String> getArguments() {
        return arguments;
    }

    /**
     * Get the thingy text elements options.
     *
     * @return options
     */
    public Map<String, String> getOptions() {
        return options;
    }

}
