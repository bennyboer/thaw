package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating a class name.
 */
public class BlockStartClassNameState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == '{') {
            ctx.popState();
            ctx.pushState(new BlockOpeningState());
        } else if (c == ',') {
            ctx.popState();
            ctx.pushState(new BlockStartSeparatorState());
        } else if (c == ':') {
            ctx.popState();
            ctx.pushState(new BlockStartPseudoClassSeparatorState());
        } else if (!Character.isLetter(c) && c != ' ' && c != '-') {
            throw new StyleFormatLexerException(String.format(
                    "A class name must only contain letters & '-' and not a character like '%c'",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_CLASS_NAME;
    }

}
