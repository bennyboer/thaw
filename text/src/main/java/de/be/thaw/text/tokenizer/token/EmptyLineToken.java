package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextRange;

/**
 * Token representing one or multiple empty lines.
 */
public class EmptyLineToken extends DefaultToken {

    public EmptyLineToken(String value, TextRange range) {
        super(value, range);
    }

    @Override
    public TokenType getType() {
        return TokenType.EMPTY_LINE;
    }

}
