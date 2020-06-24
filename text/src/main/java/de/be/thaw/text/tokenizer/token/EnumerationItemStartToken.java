package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;

/**
 * Token that represents an enumeration item start.
 */
public class EnumerationItemStartToken extends DefaultToken {

    /**
     * Default value that is applied to each enumeration item start token.
     */
    private static final String VALUE = "-";

    /**
     * Indent of the enumeration item start.
     */
    private final int indent;

    public EnumerationItemStartToken(TextPosition position, int indent) {
        super(VALUE, position);

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
