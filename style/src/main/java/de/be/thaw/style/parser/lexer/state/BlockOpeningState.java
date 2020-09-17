package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State of when a style block is opened using '{'.
 */
public class BlockOpeningState extends AbstractSFLexerState {

    @Override
    public void doProcess(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        // TODO
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_OPEN;
    }

}
