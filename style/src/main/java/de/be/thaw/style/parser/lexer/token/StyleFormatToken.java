package de.be.thaw.style.parser.lexer.token;

import de.be.thaw.util.parser.location.TextFileRange;

/**
 * Representation of a style format token.
 */
public class StyleFormatToken {

    /**
     * The value of the token.
     */
    private final String value;

    /**
     * The range of the token in the original text file.
     */
    private final TextFileRange range;

    /**
     * Get the type of the token.
     */
    private final StyleFormatTokenType type;

    public StyleFormatToken(String value, TextFileRange range, StyleFormatTokenType type) {
        this.value = value;
        this.range = range;
        this.type = type;
    }

    /**
     * Get the tokens original text range in the text file.
     *
     * @return text file range
     */
    public TextFileRange getRange() {
        return range;
    }

    /**
     * Get the value of the token.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the type of the token.
     *
     * @return type
     */
    public StyleFormatTokenType getType() {
        return type;
    }

}
