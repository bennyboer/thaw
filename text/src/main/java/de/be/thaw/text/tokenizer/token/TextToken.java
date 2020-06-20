package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextRange;

/**
 * Token only containing text.
 */
public class TextToken extends DefaultToken {

    public TextToken(String value, TextRange range) {
        super(value, range);
    }

    @Override
    public TokenType getType() {
        return TokenType.TEXT;
    }

}
