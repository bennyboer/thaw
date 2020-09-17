package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating a property value.
 */
public class ValueState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == ';') {
            ctx.popState();
            ctx.pushState(new ValueEndState());
        } else if (c == '\n') {
            throw new StyleFormatLexerException(
                    "Anticipated end of a value using a semicolon (';') character, instead got a new line ('\\n')",
                    ctx.getCurrentPosition()
            );
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.VALUE;
    }

}
