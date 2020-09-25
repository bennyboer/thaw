package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating a pseudo class setting.
 */
public class BlockStartPseudoClassSettingState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (c == ')') {
            ctx.popState();
            ctx.pushState(new BlockStartPseudoClassSettingsEndState());
        } else if (c == ',') {
            ctx.pushState(new BlockStartPseudoClassSettingSeparatorState());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_PSEUDO_CLASS_SETTING;
    }

}
