package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * Lexer state of being in a single line comment.
 */
public class SingleLineCommentState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == '\n') {
            ctx.popState(); // Leave comment state
        }
    }

    @Override
    public StyleFormatTokenType getType() throws StyleFormatLexerException {
        return StyleFormatTokenType.SINGLE_LINE_COMMENT;
    }

}
