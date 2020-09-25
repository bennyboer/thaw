package de.be.thaw.style.parser.lexer;

import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatToken;

import java.io.Reader;
import java.util.List;

/**
 * Lexer for the Thaw document style format.
 */
public interface StyleFormatLexer {

    /**
     * Process the passed style format source.
     *
     * @param reader to read style format to analyse from
     * @return a stream of read tokens
     * @throws StyleFormatLexerException in case the lexer encountered a problem
     */
    List<StyleFormatToken> process(Reader reader) throws StyleFormatLexerException;

}
