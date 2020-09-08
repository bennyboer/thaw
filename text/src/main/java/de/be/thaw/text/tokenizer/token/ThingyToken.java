package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.util.TextPosition;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Token representing a thingy.
 */
public class ThingyToken extends FormattedToken {

    /**
     * Fake value of every thingy token.
     */
    private static final String VALUE = "#";

    /**
     * The thingys name.
     */
    private final String name;

    /**
     * The thingys arguments.
     */
    private final Collection<String> arguments;

    /**
     * Map of thingy options.
     */
    private final Map<String, String> options;

    public ThingyToken(TextPosition position, String name, Collection<String> arguments, Map<String, String> options, Set<TextEmphasis> emphases) {
        super(VALUE, position, emphases);

        this.name = name;
        this.arguments = arguments;
        this.options = options;
    }

    @Override
    public TokenType getType() {
        return TokenType.THINGY;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getArguments() {
        return arguments;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return String.format(
                "%s > Name: '%s', Args: %s, Options: %s",
                super.toString(),
                getName(),
                getArguments(),
                getOptions()
        );
    }

}
