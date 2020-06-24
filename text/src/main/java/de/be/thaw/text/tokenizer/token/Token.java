package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.util.TextPosition;

/**
 * A token of the tokenizer.
 */
public interface Token {

    /**
     * Get the text value of the token.
     *
     * @return text value
     */
    String getValue();

    /**
     * Get the tokens original text position.
     *
     * @return position
     */
    TextPosition getPosition();

    /**
     * Get the type.
     *
     * @return type
     */
    TokenType getType();

}
