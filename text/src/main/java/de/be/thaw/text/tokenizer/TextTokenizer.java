package de.be.thaw.text.tokenizer;

import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.state.State;
import de.be.thaw.text.tokenizer.state.TextState;
import de.be.thaw.text.tokenizer.token.EmptyLineToken;
import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.util.TextRange;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Tokenizer for the thaw document text format.
 */
public class TextTokenizer {

    /**
     * Tokenize the text represented by the passed reader.
     * Accepted tokens are published over the passed consumer.
     *
     * @param reader        to read the text
     * @param tokenConsumer where tokens are published with
     * @throws TokenizeException in case a problem occurs
     */
    public void tokenize(Reader reader, Consumer<Token> tokenConsumer) throws TokenizeException {
        State currentState = new TextState();

        // Create tokenizing context used as interface for states to influence the reading process/accepting tokens, ...
        LinkedList<Integer> lookAheadBuffer = new LinkedList<>();
        TokenizingContext ctx = new TokenizingContext(
                tokenConsumer,
                relativePos -> {
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
        );

        int skippedNewLines = 0;
        int c;
        try {
            while ((c = lookAheadBuffer.isEmpty() ? reader.read() : lookAheadBuffer.poll()) != -1) {
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
                        // Replace with white space character
                        c = ' ';
                    } else {
                        if (ctx.bufferLength() > 0) {
                            currentState.forceEnd(ctx);
                        }
                        continue;
                    }
                } else if (skippedNewLines > 1) {
                    // One or more empty lines occurred -> force end the current state
                    skippedNewLines = 0;
                    ctx.buffer('\n');
                    ctx.buffer('\n');
                    ctx.acceptToken(v -> new EmptyLineToken(v, new TextRange(ctx.getStartPos(), ctx.getEndPos())));
                    currentState = new TextState();
                }

                currentState = currentState.translate((char) c, ctx);
            }
        } catch (IOException e) {
            throw new TokenizeException(e);
        }

        // Create last dangling token (if any).
        currentState.forceEnd(ctx);
    }

}
