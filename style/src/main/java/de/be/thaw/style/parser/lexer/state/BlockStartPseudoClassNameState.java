package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating a pseudo class name.
 */
public class BlockStartPseudoClassNameState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == '(') {
            ctx.popState();
            ctx.pushState(new BlockStartPseudoClassSettingsStartState());
        } else if (c == '{') {
            ctx.popState();
            ctx.pushState(new BlockOpeningState());
        } else if (!Character.isLetter(c)) {
            throw new StyleFormatLexerException(String.format(
                    "Unexpected letter %c in a pseudo class name",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_PSEUDO_CLASS_NAME;
    }

}
