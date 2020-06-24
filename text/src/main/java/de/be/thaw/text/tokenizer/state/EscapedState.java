package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;

/**
 * State when escaping the next character.
 */
public class EscapedState implements State {

    /**
     * State to return to after processing the next character.
     */
    private final State returnState;

    public EscapedState(State returnState) {
        this.returnState = returnState;
    }

    @Override
    public State translate(char c, TokenizingContext ctx) {
        return switch (c) {
            case '#', '_', '*', '`' -> {
                ctx.getBuffer().append(c);

                yield returnState;
            }
            default -> {
                // There was no need to escape that character -> add escape character to buffer
                ctx.getBuffer().append('\\');
                ctx.getBuffer().append(c);

                yield returnState;
            }
        };
    }

    @Override
    public void forceEnd(TokenizingContext ctx) throws InvalidStateException {
        ctx.getBuffer().append('\\');

        returnState.forceEnd(ctx);
    }

    @Override
    public State onNewLine(TokenizingContext ctx) {
        ctx.getBuffer().append('\\');
        ctx.getBuffer().append(' '); // Add white space instead of new line character

        return returnState;
    }

}
