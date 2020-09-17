package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * The start state of the lexer.
 */
public class RootState extends AbstractSFLexerState {

    @Override
    public void doProcess(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (Character.isLetter(c)) {
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
        return StyleFormatTokenType.IGNORE;
    }

}
