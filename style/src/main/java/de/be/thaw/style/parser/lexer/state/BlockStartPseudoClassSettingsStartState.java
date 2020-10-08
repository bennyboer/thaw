package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating a pseudo class setting.
 */
public class BlockStartPseudoClassSettingsStartState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (Character.isLetterOrDigit(c) || c == '*') { // *-character allowed as placeholder
            ctx.popState();
            ctx.pushState(new BlockStartPseudoClassSettingState());
        } else if (c == ')') {
            // Ending the settings without specifying arguments, which is OK I suppose
            ctx.popState();
            ctx.pushState(new BlockStartPseudoClassSettingsEndState());
        } else {
            throw new StyleFormatLexerException(String.format(
                    "Anticipated a valid pseudo class setting value (digit or letter) and not '%c'",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_PSEUDO_CLASS_SETTINGS_START;
    }

}
