package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating a block name.
 * For example when having "document { ... }", we want to extract "document" as token.
 * But we have to stop when encountering a white space, ',' (name separator),
 * ':' (pseudo class start) or '.' (class name start).
 */
public class BlockStartNameState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (Character.isDigit(c)) {
            throw new StyleFormatLexerException(String.format(
                    "Digits like %c are not allowed in a style block name",
                    c
            ), ctx.getCurrentPosition());
        } else if (c == ',') {
            ctx.popState();
            ctx.pushState(new BlockStartSeparatorState());
        } else if (c == ':') {
            ctx.popState();
            ctx.pushState(new BlockStartPseudoClassSeparatorState());
        } else if (c == '.') {
            ctx.popState();
            ctx.pushState(new BlockStartClassSeparatorState());
        } else if (c == '{') {
            ctx.popState();
            ctx.pushState(new BlockOpeningState());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_NAME;
    }

}
