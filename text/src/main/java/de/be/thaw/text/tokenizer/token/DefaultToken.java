package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;
import de.be.thaw.text.util.TextRange;

/**
 * A default token of the tokenizer.
 */
public abstract class DefaultToken implements Token {

    /**
     * Value of the token.
     */
    private final String value;

    /**
     * Range of the token in the original text.
     */
    private final TextRange range;

    /**
     * Original position of the token in the text.
     */
    private final TextPosition position;

    public DefaultToken(String value, TextRange range, TextPosition position) {
        this.value = value;
        this.range = range;
        this.position = position;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public TextRange getRange() {
        return range;
    }

    @Override
    public TextPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s, %s, '%s'", getType().name(), getRange(), getPosition(), getValue());
    }

}
