package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;

/**
 * Token only containing text.
 */
public class TextToken extends DefaultToken {

    public TextToken(String value, TextPosition position) {
        super(value, position);
    }

    @Override
    public TokenType getType() {
        return TokenType.TEXT;
    }

}
