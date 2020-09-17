package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;

/**
 * State when a block has just closed with '}'.
 */
public class BlockClosingState extends AbstractSFLexerState {

    @Override
    public void doProcess(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        ctx.popState(); // Will be done in either case

        if (Character.isLetter(c)) {
            ctx.pushState(new BlockStartNameState());
        } else if (c != ' ' && c != '\n') {
            throw new StyleFormatLexerException(String.format(
                    "Encountered unexpected character '%c' when anticipating a new block start",
                    c
            ), ctx.getCurrentPosition());
        }
    }

    @Override
    public StyleFormatTokenType getType() {
        return StyleFormatTokenType.BLOCK_CLOSE;
    }

}
