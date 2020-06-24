package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;

/**
 * Token representing one or multiple empty lines.
 */
public class EmptyLineToken extends DefaultToken {

    /**
     * Fake value to represent a new line.
     */
    private static final String VALUE = "\\n\\n";

    public EmptyLineToken(TextPosition position) {
        super(VALUE, position);
    }

    @Override
    public TokenType getType() {
        return TokenType.EMPTY_LINE;
    }

}
