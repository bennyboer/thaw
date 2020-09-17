package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State representing the end of a value declaration.
 */
public class ValueEndState extends AbstractSFLexerState {

    @Override
    public void doProcess(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (Character.isLetter(c)) {
            ctx.popState();
            ctx.pushState(new PropertyState());
        } else if (c == '}') {
            ctx.popState();
            ctx.pushState(new BlockClosingState());
        } else if (c != ' ' && c != '\n') {
            throw new StyleFormatLexerException(String.format(
                    "Encountered unexpected character '%c' when anticipating a new property start or a style block end",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.VALUE_END;
    }

}
