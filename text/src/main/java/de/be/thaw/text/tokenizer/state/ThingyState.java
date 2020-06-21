package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.token.ThingyToken;
import de.be.thaw.text.util.TextRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * State of a thingy.
 */
public class ThingyState implements State {

    /**
     * State to translate to after the thingy finished.
     */
    private final State returnState;

    /**
     * The internal state of the thingy state.
     */
    private InternalState internalState = InternalState.NAME;

    /**
     * Name of the thingy.
     */
    private String name;

    /**
     * Currently found arguments.
     */
    private final Collection<String> arguments = new ArrayList<>();

    /**
     * Currently found options.
     */
    private final Map<String, String> options = new HashMap<>();

    /**
     * A pending option key that has not yet got a value.
     */
    private String pendingOptionKey;

    public ThingyState(State returnState) {
        this.returnState = returnState;
    }

    @Override
    public State translate(char c, TokenizingContext ctx) throws InvalidStateException {
        return switch (c) {
            case '#' -> {
                switch (internalState) {
                    case NAME -> {
                        if (name == null) {
                            name = ctx.readValueAndReset().trim();

                            if (name.isEmpty()) {
                                throw new InvalidStateException(String.format("Thingy ending with '#' around position %d:%d must have a name", ctx.getLineNum(), ctx.getInLineOffset()));
                            }
                        }
                    }
                    case ARGUMENTS -> {
                        arguments.add(ctx.readValueAndReset().trim());
                    }
                    case OPTIONS -> {
                        options.put(pendingOptionKey, ctx.readValueAndReset().trim());
                    }
                }

                // Add fake value to buffer in order to be able to accept a token
                ctx.buffer('#');
                ctx.acceptToken(value -> new ThingyToken(
                        value,
                        new TextRange(ctx.getStartPos(), ctx.getEndPos()),
                        name,
                        arguments,
                        options
                ));

                yield returnState;
            }
            case '\\' -> new EscapedState(this);
            case ',' -> {
                switch (internalState) {
                    case NAME -> {
                        internalState = InternalState.ARGUMENTS;
                        name = ctx.readValueAndReset().trim();

                        if (name.isEmpty()) {
                            throw new InvalidStateException(String.format("Thingy around position %d:%d must have a non-empty name", ctx.getLineNum(), ctx.getInLineOffset()));
                        }
                    }
                    case ARGUMENTS -> {
                        arguments.add(ctx.readValueAndReset().trim());
                    }
                    case OPTIONS -> {
                        options.put(pendingOptionKey, ctx.readValueAndReset().trim());
                    }
                }
                yield this;
            }
            case '=' -> {
                switch (internalState) {
                    case NAME -> ctx.buffer(c);
                    case ARGUMENTS -> {
                        // This is not an argument but an option!
                        internalState = InternalState.OPTIONS;
                        pendingOptionKey = ctx.readValueAndReset().trim();
                    }
                    case OPTIONS -> {
                        pendingOptionKey = ctx.readValueAndReset().trim();
                    }
                }
                yield this;
            }
            default -> {
                ctx.buffer(c);
                yield this;
            }
        };
    }

    @Override
    public void forceEnd(TokenizingContext ctx) throws InvalidStateException {
        throw new InvalidStateException("Anticipated thingy end '#' before text end");
    }

    /**
     * Internal state of the thingy state.
     */
    private enum InternalState {
        NAME,
        ARGUMENTS,
        OPTIONS
    }

}
