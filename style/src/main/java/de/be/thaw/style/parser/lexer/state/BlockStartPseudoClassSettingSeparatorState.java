package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating another pseudo class setting value.
 */
public class BlockStartPseudoClassSettingSeparatorState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (Character.isLetterOrDigit(c) || c == '*') { // *-character allowed as placeholder
            ctx.popState();
        } else if (c != ' ') {
            throw new StyleFormatLexerException(String.format(
                    "Anticipated a valid pseudo class setting value (digit or letter) and not '%c'",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_PSEUDO_CLASS_SETTING_SEPARATOR;
    }

}
