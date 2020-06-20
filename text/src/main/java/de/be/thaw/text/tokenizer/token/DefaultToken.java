package de.be.thaw.text.tokenizer.token;

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

    public DefaultToken(String value, TextRange range) {
        this.value = value;
        this.range = range;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public TextRange getRange() {
        return range;
    }

}
