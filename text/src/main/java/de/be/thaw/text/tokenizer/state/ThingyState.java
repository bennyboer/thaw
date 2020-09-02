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
        OPTIONS,
        IN_STRING
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

    /**
     * Char that started the string.
     */
    private char stringStartChar = '\0';

    /**
     * State before being in the in-string state.
     */
    private InternalState beforeInStringState;

    public ThingyState(State returnState) {
        this.returnState = returnState;
    }

    @Override
    public State translate(char c, TokenizingContext ctx) throws InvalidStateException {
        if (internalState == InternalState.IN_STRING) {
            if (c == stringStartChar) {
                // Leave in-string state
                internalState = beforeInStringState;
            } else {
                ctx.getBuffer().append(c);
            }

            return this;
        } else {
            return switch (c) {
                case '#' -> handleThingyEnd(ctx);
                case ',' -> handlePushArgOption(ctx);
                case '=' -> handleOptionKey(ctx);
                case '\'', '"' -> handleString(ctx, c);
                case '\\' -> new EscapedState(this);
                default -> {
                    ctx.getBuffer().append(c);

                    yield this;
                }
            };
        }
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
        if (internalState == InternalState.IN_STRING) {
            ctx.getBuffer().append('\n');
        } else {
            // Thingy is able to stretch over multiple lines.
            // New line is then replaced with white space character.
            ctx.getBuffer().append(' ');
        }

        return this;
    }

    @Override
    public boolean acceptEmptyLine(TokenizingContext ctx) {
        return internalState == InternalState.IN_STRING;
    }

    /**
     * Handle starting or ending a string.
     *
     * @param ctx the tokenizing context
     * @param c   character that starts or ends the string
     * @return the next state
     * @throws InvalidStateException in case something went wrong
     */
    private State handleString(TokenizingContext ctx, char c) throws InvalidStateException {
        // Enter the in-string state
        stringStartChar = c;
        beforeInStringState = internalState;

        internalState = InternalState.IN_STRING;

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
                pendingOptionKey = ctx.readBufferAndReset().trim().toLowerCase(); // Save the pending option key
            }
            case OPTIONS -> pendingOptionKey = ctx.readBufferAndReset().trim().toLowerCase(); // Save the pending option key
        }

        return this;
    }

}
