package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating the start of a value.
 */
public class PropertyValueSeparatorState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c != ' ' && c != '\n') {
            ctx.popState();
            ctx.pushState(new ValueState());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.PROPERTY_VALUE_SEPARATOR;
    }

}
