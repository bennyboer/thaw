package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State at the end of a pseudo class including settings.
 */
public class BlockStartPseudoClassSettingsEndState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == ',') {
            ctx.popState();
            ctx.pushState(new BlockStartSeparatorState());
        } else if (c == '{') {
            ctx.popState();
            ctx.pushState(new BlockOpeningState());
        } else if (c != ' ') {
            throw new StyleFormatLexerException(String.format(
                    "Encountered character '%c', when we expected either ',', '{', or ' '",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_PSEUDO_CLASS_SETTINGS_END;
    }

}
