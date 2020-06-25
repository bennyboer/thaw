package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.token.FormattedToken;
import de.be.thaw.text.util.TextPosition;

import java.util.Collections;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * A state of formatted text.
 */
public class FormattedState implements State {

    /**
     * Current emphases.
     */
    private final Stack<TextEmphasis> emphases = new Stack<>();

    public FormattedState(TextEmphasis emphasis) {
        emphases.push(emphasis);
    }

    @Override
    public State translate(char c, TokenizingContext ctx) throws InvalidStateException {
        // Handle depending on the current most upper emphasis
        return switch (emphases.peek()) {
            case BOLD -> handleBold(c, ctx);
            case ITALIC -> handleItalic(c, ctx);
            case UNDERLINED -> handleUnderlined(c, ctx);
            case CODE -> handleCode(c, ctx);
        };
    }

    @Override
    public void forceEnd(TokenizingContext ctx) throws InvalidStateException {
        // Ending in this state is illegal!
        throw new InvalidStateException(
                String.format(
                        "Missing end modifiers for the following open formatting blocks at the end of the text: %s",
                        emphases.stream()
                                .sorted()
                                .map(TextEmphasis::name)
                                .collect(Collectors.joining())
                )
        );
    }

    @Override
    public State onNewLine(TokenizingContext ctx) {
        // State does ignore new lines -> Add white space instead of new line character
        ctx.getBuffer().append(' ');

        return this;
    }

    /**
     * Handle the translation when a code emphasis is the uppermost emphasis.
     *
     * @param c   character to process
     * @param ctx of the tokenizing process
     * @return the next state
     */
    private State handleCode(char c, TokenizingContext ctx) {
        return switch (c) {
            case '`' -> { // End code emphasis
                ctx.accept((value, pos) -> new FormattedToken(value, pos, Collections.singleton(TextEmphasis.CODE)));

                emphases.pop();
                if (emphases.isEmpty()) {
                    yield new TextState();
                } else {
                    yield this;
                }
            }
            case '\\' -> new EscapedState(this);
            default -> {
                ctx.getBuffer().append(c);

                yield this;
            }
        };
    }

    /**
     * Handle the translation when a underlined emphasis is the uppermost emphasis.
     *
     * @param c   character to process
     * @param ctx of the tokenizing process
     * @return the next state
     */
    private State handleUnderlined(char c, TokenizingContext ctx) {
        return switch (c) {
            case '_' -> { // End underline emphasis
                ctx.accept((value, pos) -> new FormattedToken(value, pos, Set.copyOf(emphases)));

                emphases.pop();
                if (emphases.isEmpty()) {
                    yield new TextState();
                } else {
                    yield this;
                }
            }
            default -> handleDefault(c, ctx);
        };
    }

    /**
     * Handle the translation when a italic emphasis is the uppermost emphasis.
     *
     * @param c   character to process
     * @param ctx of the tokenizing process
     * @return the next state
     */
    private State handleItalic(char c, TokenizingContext ctx) throws InvalidStateException {
        return switch (c) {
            case '*' -> { // Start bold block or end italic block
                int currentLength = ctx.getBuffer().length();
                boolean isStartBold = currentLength == 0 && !emphases.contains(TextEmphasis.BOLD);

                if (isStartBold) {
                    // Start a new bold block
                    emphases.pop();
                    emphases.push(TextEmphasis.BOLD);

                    yield this;
                } else {
                    try {
                        int nextChar = ctx.lookAhead(1);
                        if (nextChar != -1) {
                            isStartBold = ((char) nextChar) == '*';
                        }
                    } catch (TokenizeException e) {
                        throw new InvalidStateException(String.format(
                                "Look ahead by one character went wrong at %d:%d",
                                ctx.getCurrentLine(),
                                ctx.getCurrentPos()
                        ), e);
                    }

                    if (isStartBold && !emphases.contains(TextEmphasis.BOLD)) {
                        if (currentLength > 0) {
                            // Accept current token first
                            ctx.accept(new FormattedToken(
                                    ctx.readBufferAndReset(),
                                    new TextPosition(
                                            ctx.getStartLine(),
                                            ctx.getStartPos(),
                                            ctx.getCurrentLine(),
                                            ctx.getCurrentPos() - 1
                                    ),
                                    Set.copyOf(emphases)
                            ));
                        }

                        ctx.ignoreNext(1);
                        emphases.push(TextEmphasis.BOLD);

                        yield this;
                    } else {
                        // End the italic block
                        ctx.accept((value, pos) -> new FormattedToken(value, pos, Set.copyOf(emphases)));

                        emphases.pop();
                        if (emphases.isEmpty()) {
                            yield new TextState();
                        } else {
                            yield this;
                        }
                    }
                }
            }
            default -> handleDefault(c, ctx);
        };
    }

    /**
     * Handle the translation when a bold emphasis is the uppermost emphasis.
     *
     * @param c   character to process
     * @param ctx of the tokenizing process
     * @return the next state
     */
    private State handleBold(char c, TokenizingContext ctx) throws InvalidStateException {
        return switch (c) {
            case '*' -> { // May end bold block or start italic block
                // We need to look ahead one character!
                boolean isEndBold;
                try {
                    int nextChar = ctx.lookAhead(1);
                    if (nextChar == -1) {
                        // Text to tokenize ended
                        ctx.getBuffer().append(c);

                        yield this;
                    }

                    isEndBold = ((char) nextChar) == '*';
                } catch (TokenizeException e) {
                    throw new InvalidStateException(String.format(
                            "Look ahead by one character went wrong at %d:%d",
                            ctx.getCurrentLine(),
                            ctx.getCurrentPos()
                    ), e);
                }

                if (isEndBold) {
                    ctx.ignoreNext(1); // Ignore the next star character

                    // End the bold block
                    ctx.accept((value, pos) -> new FormattedToken(
                            value,
                            new TextPosition(
                                    ctx.getStartLine(),
                                    ctx.getStartPos(),
                                    ctx.getCurrentLine(),
                                    ctx.getCurrentPos() + 1
                            ),
                            Set.copyOf(emphases)
                    ));

                    emphases.pop();
                    if (emphases.isEmpty()) {
                        yield new TextState();
                    } else {
                        yield this;
                    }
                } else {
                    yield handleDefault(c, ctx);
                }
            }
            default -> handleDefault(c, ctx);
        };
    }

    /**
     * The default handler.
     *
     * @param c   to process
     * @param ctx the tokenizing context
     * @return the next state
     */
    private State handleDefault(char c, TokenizingContext ctx) {
        return switch (c) {
            case '*' -> { // Enter italic block
                ctx.accept(createEarlyToken(ctx));

                emphases.push(TextEmphasis.ITALIC);
                yield this;
            }
            case '_' -> { // Enter underline block
                ctx.accept(createEarlyToken(ctx));

                emphases.push(TextEmphasis.UNDERLINED);
                yield this;
            }
            case '`' -> { // Enter code block
                ctx.accept(createEarlyToken(ctx));

                emphases.push(TextEmphasis.CODE);
                yield this;
            }
            case '#' -> { // Enter thingy block
                ctx.accept(createEarlyToken(ctx));

                yield new ThingyState(this);
            }
            case '\\' -> new EscapedState(this);
            default -> {
                ctx.getBuffer().append(c);

                yield this;
            }
        };
    }

    /**
     * Create token that ended one character before.
     *
     * @param ctx to create token with
     * @return the created token
     */
    private FormattedToken createEarlyToken(TokenizingContext ctx) {
        return new FormattedToken(
                ctx.readBufferAndReset(),
                new TextPosition(
                        ctx.getStartLine(),
                        ctx.getStartPos(),
                        ctx.getCurrentLine(),
                        ctx.getCurrentPos() - 1
                ),
                Set.copyOf(emphases)
        );
    }

}
