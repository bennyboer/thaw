package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State expecting characters for a property.
 */
public class PropertyState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == ':') {
            ctx.popState();
            ctx.pushState(new PropertyValueSeparatorState());
        } else if (!Character.isLetter(c) && c != ' ' && c != '-') {
            throw new StyleFormatLexerException(String.format(
                    "Expected a property name to only consist of letters and '-', instead got '%c'",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.PROPERTY;
    }

}
