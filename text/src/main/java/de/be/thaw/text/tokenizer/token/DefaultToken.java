package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;

/**
 * A default token of the tokenizer.
 */
public abstract class DefaultToken implements Token {

    /**
     * Value of the token.
     */
    private final String value;

    /**
     * Original position of the token in the text.
     */
    private final TextPosition position;

    public DefaultToken(String value, TextPosition position) {
        this.value = value;
        this.position = position;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public TextPosition getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s, '%s'", getType().name(), getPosition(), getValue());
    }

}
