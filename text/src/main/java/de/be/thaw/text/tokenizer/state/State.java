package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;

/**
 * State of the tokenizer.
 */
public interface State {

    /**
     * Translate into another state using the character c.
     *
     * @param c   to translate into another state
     * @param ctx current context of the tokenizing process
     * @return the next state (may be the same)
     * @throws InvalidStateException in case the character c is not allowed and leads to an invalid state
     */
    State translate(char c, TokenizingContext ctx) throws InvalidStateException;

    /**
     * Called when the text has been completely read.
     * State might need to add the rest of the parsed text as token or throw an exception.
     *
     * @param ctx the tokenizing context
     */
    void forceEnd(TokenizingContext ctx) throws InvalidStateException;

}
