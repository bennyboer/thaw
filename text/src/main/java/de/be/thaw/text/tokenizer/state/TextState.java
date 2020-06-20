package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.model.element.emphasis.TextEmphasis;
import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.token.TextToken;
import de.be.thaw.text.util.TextRange;

/**
 * State of standard text.
 */
public class TextState implements State {

    @Override
    public State translate(char c, TokenizingContext ctx) throws InvalidStateException {
        return switch (c) {
            case '*' -> {
                ctx.acceptToken(value -> new TextToken(value, new TextRange(ctx.getStartPos(), ctx.getEndPos() - 1)));
                yield new FormattedState(TextEmphasis.ITALIC);
            }
            case '_' -> {
                ctx.acceptToken(value -> new TextToken(value, new TextRange(ctx.getStartPos(), ctx.getEndPos() - 1)));
                yield new FormattedState(TextEmphasis.UNDERLINED);
            }
            case '`' -> {
                ctx.acceptToken(value -> new TextToken(value, new TextRange(ctx.getStartPos(), ctx.getEndPos() - 1)));
                yield new FormattedState(TextEmphasis.CODE);
            }
            case '\\' -> new EscapedState(this);
            default -> {
                ctx.buffer(c);
                yield this;
            }
        };
    }

    @Override
    public void forceEnd(TokenizingContext ctx) throws InvalidStateException {
        // Read end is always OK for the text state -> just accepting the last dangling token
        ctx.acceptToken(value -> new TextToken(value, new TextRange(ctx.getStartPos(), ctx.getEndPos())));
    }

}
