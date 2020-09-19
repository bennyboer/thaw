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
        } else if (c == '}') {
            ctx.popState();
            ctx.pushState(new BlockClosingState());
        } else if (c == ':') {
            throw new StyleFormatLexerException(
                    "Encountered ':' character in a value. Most likely you forgot to add a semicolon (';') after a property?",
                    ctx.getCurrentPosition()
            );
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.VALUE;
    }

}
