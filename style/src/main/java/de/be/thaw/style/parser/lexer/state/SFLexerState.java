package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State of the style format lexer.
 */
public interface SFLexerState {

    /**
     * Process the passed character.
     *
     * @param c   to process
     * @param ctx the lexer context
     * @throws StyleFormatLexerException in case a problem occurs
     */
    void process(char c, SFLexerContext ctx) throws StyleFormatLexerException;

    /**
     * Get the token type this state is representing.
     *
     * @return token type
     */
    StyleFormatTokenType getType();

}
