package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.model.element.emphasis.TextEmphasis;
import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.state.util.StateUtil;
import de.be.thaw.text.tokenizer.token.EnumerationItemStartToken;
import de.be.thaw.text.tokenizer.token.TextToken;
import de.be.thaw.text.util.TextPosition;

/**
 * State of standard text.
 */
public class TextState implements State {

    @Override
    public State translate(char c, TokenizingContext ctx) throws InvalidStateException {
        return switch (c) {
            case '*' -> {
                ctx.accept(createEarlyToken(ctx));

                yield new FormattedState(TextEmphasis.ITALIC);
            }
            case '_' -> {
                ctx.accept(createEarlyToken(ctx));

                yield new FormattedState(TextEmphasis.UNDERLINED);
            }
            case '`' -> {
                ctx.accept(createEarlyToken(ctx));

                yield new FormattedState(TextEmphasis.CODE);
            }
            case '#' -> {
                ctx.accept(createEarlyToken(ctx));

                yield new ThingyState(new TextState());
            }
            case '-' -> {
                // Check if is enumeration item start (first non-whitespace character in line)
                if (!onMayBeEnumerationStart(ctx)) {
                    ctx.getBuffer().append(c);
                }

                yield this;
            }
            case '\\' -> new EscapedState(this);
            default -> {
                ctx.getBuffer().append(c);

                yield this;
            }
        };
    }

    /**
     * Check whether the current minus character is an enumeration start.
     * If it is the token will be applied.
     *
     * @param ctx the tokenizing context
     * @return whether it is an enumeration start
     * @throws InvalidStateException in case a look ahead went wrong
     */
    private boolean onMayBeEnumerationStart(TokenizingContext ctx) throws InvalidStateException {
        if (!StateUtil.isFirstNonWhiteSpaceCharacterInLine(ctx)) {
            return false;
        }

        // Check if the next character is a whitespace which is required for enumeration starts
        int nextChar;
        try {
            nextChar = ctx.lookAhead(1);
            if (nextChar == -1) {
                throw new InvalidStateException("Anticipated at least another character in the text");
            }
        } catch (TokenizeException e) {
            throw new InvalidStateException("Look ahead went wrong", e);
        }

        if ((char) nextChar != ' ') {
            return false; // Enumeration start needs to have a white space after the '-'-character
        }

        ctx.ignoreNext(1); // Ignore the next character which is a white space

        int indent = ctx.getCurrentPos() - 1;

        String value = ctx.getBuffer().substring(0, ctx.getBuffer().length() - indent);

        // Accept text token first
        int currentPos = ctx.getCurrentPos(); // Save for later
        ctx.accept(new TextToken(value, new TextPosition(ctx.getStartLine(), ctx.getStartPos(), ctx.getCurrentLine() - 1, ctx.getLastLineLength())));

        // Restore correct text position
        ctx.setStartLine(ctx.getStartLine() + 1);
        ctx.setStartPos(1);
        ctx.setCurrentLine(ctx.getStartLine());
        ctx.setCurrentPos(currentPos);

        // Clear buffer
        ctx.getBuffer().setLength(0);

        // Accept enumeration item start token
        ctx.accept(new EnumerationItemStartToken(new TextPosition(ctx.getStartLine(), ctx.getStartPos(), ctx.getCurrentLine(), ctx.getCurrentPos() + 1), indent));

        return true;
    }

    @Override
    public void forceEnd(TokenizingContext ctx) {
        // Read end is always OK for the text state -> just accepting the last dangling token
        ctx.accept(TextToken::new);
    }

    @Override
    public State onNewLine(TokenizingContext ctx) {
        // State ignores new line character and replaces it with a white space character
        ctx.getBuffer().append(' ');
        return this;
    }

    /**
     * Create token that ended one character before.
     *
     * @param ctx to create token with
     * @return the created token
     */
    private TextToken createEarlyToken(TokenizingContext ctx) {
        return new TextToken(
                ctx.readBufferAndReset(),
                new TextPosition(
                        ctx.getStartLine(),
                        ctx.getStartPos(),
                        ctx.getCurrentLine(),
                        ctx.getCurrentPos() - 1
                )
        );
    }

}
