package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State of being between multiple block start names.
 */
public class BlockStartSeparatorState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (Character.isLetter(c)) {
            ctx.popState();
            ctx.pushState(new BlockStartNameState());
        } else if (Character.isDigit(c)) {
            throw new StyleFormatLexerException(String.format(
                    "Digits like %c are not allowed here",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_SEPARATOR;
    }

}
