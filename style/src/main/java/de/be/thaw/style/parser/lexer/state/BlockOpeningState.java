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
        if (Character.isLetter(c)) {
            ctx.popState();
            ctx.pushState(new PropertyState());
        } else if (c == '}') {
            ctx.popState();
            ctx.pushState(new BlockClosingState());
        } else if (Character.isDigit(c)) {
            throw new StyleFormatLexerException(String.format(
                    "Encountered illegal digit character '%c' when awaiting a property start",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_OPEN;
    }

}
