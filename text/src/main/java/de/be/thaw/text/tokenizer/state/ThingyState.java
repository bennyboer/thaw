package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.token.ThingyToken;
import de.be.thaw.text.util.TextPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * State of a thingy.
 */
public class ThingyState implements State {

    /**
     * Internal state of the thingy state.
     */
    private enum InternalState {
        NAME,
        ARGUMENTS,
        OPTIONS
    }

    /**
     * The internal state of the thingy state.
     */
    private InternalState internalState = InternalState.NAME;

    /**
     * State to translate to after the thingy finished.
     */
    private final State returnState;

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
            case '#' -> handleThingyEnd(ctx);
            case ',' -> handlePushArgOption(ctx);
            case '=' -> handleOptionKey(ctx);
            case '\\' -> new EscapedState(this);
            default -> {
                ctx.getBuffer().append(c);

                yield this;
            }
        };
    }

    @Override
    public void forceEnd(TokenizingContext ctx) throws InvalidStateException {
        throw new InvalidStateException(
                String.format(
                        "Anticipated thingy end '#' of thingy starting at %d:%d before text end",
                        ctx.getStartLine(),
                        ctx.getStartPos()
                )
        );
    }

    @Override
    public State onNewLine(TokenizingContext ctx) {
        // Thingy is able to stretch over multiple lines.
        // New line is then replaced with white space character.
        ctx.getBuffer().append(' ');

        return this;
    }

    /**
     * Handle the thingy end with the #-character.
     *
     * @param ctx the tokenizing context
     * @return the next state
     * @throws InvalidStateException in case something went wrong
     */
    private State handleThingyEnd(TokenizingContext ctx) throws InvalidStateException {
        switch (internalState) {
            case NAME -> {
                if (name == null) {
                    name = ctx.readBufferAndReset().trim();

                    if (name.isEmpty()) {
                        throw new InvalidStateException(String.format(
                                "Thingy ending with '#' at %d:%d must have a name",
                                ctx.getCurrentLine(),
                                ctx.getCurrentPos()
                        ));
                    }
                }
            }
            case ARGUMENTS -> arguments.add(ctx.readBufferAndReset().trim()); // Push argument
            case OPTIONS -> options.put(pendingOptionKey, ctx.readBufferAndReset().trim()); // Push option
        }

        // Accept the finished thingy token
        ctx.accept(new ThingyToken(
                new TextPosition(
                        ctx.getStartLine(),
                        ctx.getStartPos(),
                        ctx.getCurrentLine(),
                        ctx.getCurrentPos()
                ),
                name,
                arguments,
                options
        ));

        return returnState;
    }

    /**
     * Handle a push of the current argument or option caused by a comma.
     *
     * @param ctx the tokenizing context
     * @return the next state
     */
    private State handlePushArgOption(TokenizingContext ctx) throws InvalidStateException {
        switch (internalState) {
            case NAME -> {
                internalState = InternalState.ARGUMENTS;
                name = ctx.readBufferAndReset().trim();

                if (name.isEmpty()) {
                    throw new InvalidStateException(
                            String.format(
                                    "Thingy starting at %d:%d must have a non-empty name",
                                    ctx.getStartLine(),
                                    ctx.getStartPos()
                            )
                    );
                }
            }
            case ARGUMENTS -> arguments.add(ctx.readBufferAndReset().trim()); // Push argument
            case OPTIONS -> options.put(pendingOptionKey, ctx.readBufferAndReset().trim()); // Push option
        }

        return this;
    }

    /**
     * Handle the option key storing caused by the '='-character.
     *
     * @param ctx the tokenizing context.
     * @return the next state
     */
    private State handleOptionKey(TokenizingContext ctx) {
        switch (internalState) {
            case NAME -> ctx.getBuffer().append('='); // '='-character is allowed as thingy name character
            case ARGUMENTS -> {
                // This is not an argument but an option -> change internal state!
                internalState = InternalState.OPTIONS;
                pendingOptionKey = ctx.readBufferAndReset().trim(); // Save the pending option key
            }
            case OPTIONS -> pendingOptionKey = ctx.readBufferAndReset().trim(); // Save the pending option key
        }

        return this;
    }

}
