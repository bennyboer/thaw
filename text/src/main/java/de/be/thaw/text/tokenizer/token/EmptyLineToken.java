package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;
import de.be.thaw.text.util.TextRange;

/**
 * Token representing one or multiple empty lines.
 */
public class EmptyLineToken extends DefaultToken {

    public EmptyLineToken(String value, TextRange range, TextPosition position) {
        super(value, range, position);
    }

    @Override
    public TokenType getType() {
        return TokenType.EMPTY_LINE;
    }

}
