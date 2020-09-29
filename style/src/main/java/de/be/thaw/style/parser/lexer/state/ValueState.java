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
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.VALUE;
    }

}
