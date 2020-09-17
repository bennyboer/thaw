package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * Lexer state of being in a multi line comment.
 */
public class MultiLineCommentState implements SFLexerState {

    /**
     * Whether we may leave the comment state the next character (when '/').
     */
    private boolean mayLeave = false;

    @Override
    public void process(char c, SFLexerContext ctx) {
        if (mayLeave) {
            if (c == '/') {
                ctx.popStateAfter();
            } else {
                mayLeave = false;
            }
        }

        if (c == '*') {
            mayLeave = true;
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.MULTI_LINE_COMMENT;
    }

}
