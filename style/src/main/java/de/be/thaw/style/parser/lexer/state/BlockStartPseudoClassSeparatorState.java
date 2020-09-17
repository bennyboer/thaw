package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State anticipating a pseudo class name.
 */
public class BlockStartPseudoClassSeparatorState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        if (Character.isLetter(c)) {
            ctx.popState();
            ctx.pushState(new BlockStartPseudoClassNameState());
        } else {
            throw new StyleFormatLexerException(String.format(
                    "Anticipated a pseudo class name here, instead got %c",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_START_PSEUDO_CLASS_SEPARATOR;
    }

}
