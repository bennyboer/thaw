package de.be.thaw.text.tokenizer;

import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.state.State;
import de.be.thaw.text.tokenizer.state.TextState;
import de.be.thaw.text.tokenizer.token.EmptyLineToken;
import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.tokenizer.util.result.Result;
import de.be.thaw.text.util.TextRange;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Tokenizer for the thaw document text format.
 */
public class TextTokenizer implements Iterator<Result<Token, TokenizeException>> {

    /**
     * The current tokenizer state.
     */
    private State currentState = new TextState();

    /**
     * Amount of consecutive skipped new line characters.
     */
    private int skippedNewLines = 0;

    /**
     * Tokens currently buffered and ready to be received.
     */
    private final Queue<Token> tokenBuffer = new LinkedList<>();

    /**
     * Buffer for looked ahead characters.
     */
    private final LinkedList<Integer> lookAheadBuffer = new LinkedList<>();

    /**
     * Context used during tokenizing.
     */
    private final TokenizingContext ctx = new TokenizingContext(tokenBuffer::add, this::lookAhead);

    /**
     * The reader to read the text to be tokenized from.
     */
    private final Reader reader;

    public TextTokenizer(Reader reader) throws TokenizeException {
        this.reader = reader;

        // Get initial token
        tokenizeNext();
    }

    @Override
    public boolean hasNext() {
        return !tokenBuffer.isEmpty();
    }

    @Override
    public Result<Token, TokenizeException> next() {
        Token toReturn = tokenBuffer.poll();

        try {
            tokenizeNext();
        } catch (TokenizeException e) {
            return Result.error(e);
        }

        return Result.success(toReturn);
    }

    /**
     * Tokenize the next token.
     */
    private void tokenizeNext() throws TokenizeException {
        int oldLen = tokenBuffer.size();

        int c = -1;
        try {
            while (tokenBuffer.size() == oldLen && (c = lookAheadBuffer.isEmpty() ? reader.read() : lookAheadBuffer.poll()) != -1) {
                if (ctx.getIgnoreCounter() > 0) {
                    ctx.decrementIgnoreCounter();
                    continue;
                } else {
                    ctx.incEndPos(); // Move current end position one further
                }

                // Execute some special actions for certain characters
                if (c == '\r') {
                    // Skip the carriage return character
                    continue;
                } else if (c == '\n') {
                    // Skip the new line character.
                    ctx.incLineNum();
                    skippedNewLines++;
                    if (skippedNewLines == 1) {
                        // Let state handle new line character
                        currentState = currentState.onNewLine(ctx);
                    } else {
                        if (ctx.bufferLength() > 0) {
                            currentState.forceEnd(ctx);
                        }
                    }
                    continue;
                } else if (skippedNewLines > 0) {
                    // One or more empty lines occurred
                    boolean multipleNewLineSkips = skippedNewLines > 1;

                    skippedNewLines = 0; // Reset

                    if (multipleNewLineSkips) {
                        ctx.buffer('\n');
                        ctx.buffer('\n');
                        ctx.acceptToken(v -> new EmptyLineToken(v, new TextRange(ctx.getStartPos(), ctx.getEndPos())));
                        currentState = new TextState();
                    }
                }

                currentState = currentState.translate((char) c, ctx);
            }

            if (c == -1) {
                // Create last dangling token (if any).
                currentState.forceEnd(ctx);
            }
        } catch (IOException | InvalidStateException e) {
            throw new TokenizeException(e);
        }
    }

    /**
     * Look ahead the character at the passed relativePos.
     * Where relativePos = 1 means the next character.
     *
     * @param relativePos to look ahead
     * @return the character at the passed relative position or -1 if the end of the stream has been reached
     * @throws TokenizeException in case looking ahead went wrong
     */
    private int lookAhead(int relativePos) throws TokenizeException {
        if (relativePos <= 0) {
            throw new IllegalArgumentException("Cannot lookahead 0 or less characters");
        }

        int toReadCount = relativePos - lookAheadBuffer.size();
        for (int i = 0; i < toReadCount; i++) {
            try {
                int next = reader.read();
                if (next == -1) {
                    return -1; // End of stream reached
                }

                lookAheadBuffer.add(next);
            } catch (IOException e) {
                throw new TokenizeException(e);
            }
        }

        return lookAheadBuffer.get(relativePos - 1);
    }

}
