package de.be.thaw.text.tokenizer.state;

import de.be.thaw.text.model.element.emphasis.TextEmphasis;
import de.be.thaw.text.tokenizer.TokenizingContext;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.state.util.StateUtil;
import de.be.thaw.text.tokenizer.token.EnumerationItemStartToken;
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
            case '#' -> {
                ctx.acceptToken(value -> new TextToken(value, new TextRange(ctx.getStartPos(), ctx.getEndPos() - 1)));
                yield new ThingyState(new TextState());
            }
            case '-' -> {
                // Check if is enumeration item start (first non-whitespace character in line)
                if (StateUtil.isFirstNonWhiteSpaceCharacterInLine(ctx)) {
                    int nextChar;
                    try {
                        nextChar = ctx.lookAhead(1);
                        if (nextChar == -1) {
                            throw new InvalidStateException("Anticipated at least another character in the text");
                        }
                    } catch (TokenizeException e) {
                        throw new InvalidStateException("Look ahead went wrong", e);
                    }

                    if ((char) nextChar == ' ') { // Enumeration needs to have a white space after the '-'-character
                        ctx.ignoreNext(1);

                        int indent = ctx.getInLineOffset() - 2;

                        int curEndPos = ctx.getEndPos();

                        // Accept text token first
                        ctx.acceptToken(value -> new TextToken(value.substring(0, value.length() - indent), new TextRange(ctx.getStartPos(), ctx.getEndPos() - 1 - indent)));

                        ctx.buffer('-'); // Buffer symbolic enumeration character
                        ctx.acceptToken(value -> new EnumerationItemStartToken(value, new TextRange(ctx.getStartPos(), curEndPos + 1), indent));
                        ctx.setEndPos(curEndPos + 1);

                        yield this;
                    }
                }

                // Normal character
                ctx.buffer(c);
                yield this;
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

    @Override
    public State onNewLine(TokenizingContext ctx) throws InvalidStateException {
        // State ignores new line character and replaces it with a white space character
        ctx.buffer(' ');
        return this;
    }

}
