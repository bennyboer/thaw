package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

import java.util.Optional;

/**
 * Lexer state of being in a multi line comment.
 */
public class MultiLineCommentState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == '*') {
            // Check if we have to leave the multi-line comment state
            Optional<Character> optNextChar = ctx.lookAhead(1);
            if (optNextChar.isPresent()) {
                char nextChar = optNextChar.orElseThrow();
                if (nextChar == '/') {
                    ctx.popState();
                }
            }
        }
    }

    @Override
    public StyleFormatTokenType getType() throws StyleFormatLexerException {
        return StyleFormatTokenType.MULTI_LINE_COMMENT;
    }

}
