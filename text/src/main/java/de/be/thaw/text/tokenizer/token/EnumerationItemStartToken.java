package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;
import de.be.thaw.text.util.TextRange;

/**
 * Token that represents an enumeration item start.
 */
public class EnumerationItemStartToken extends DefaultToken {

    /**
     * Indent of the enumeration item start.
     */
    private final int indent;

    public EnumerationItemStartToken(String value, TextRange range, TextPosition position, int indent) {
        super(value, range, position);

        this.indent = indent;
    }

    @Override
    public TokenType getType() {
        return TokenType.ENUMERATION_ITEM_START;
    }

    /**
     * Get the indent of the item start.
     *
     * @return indent
     */
    public int getIndent() {
        return indent;
    }

}
