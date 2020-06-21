package de.be.thaw.text.tokenizer;

import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.tokenizer.util.RethrowingFunction;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Current tokenizing context used during tokenizing.
 */
public class TokenizingContext {

    /**
     * Start position of the current token in the original text.
     */
    private int startPos = 0;

    /**
     * Current end position of the current token in the original text.
     */
    private int endPos = 0;

    /**
     * Number of the line currently tokenizing.
     */
    private int lineNum = 1;

    /**
     * Offset in line where currently tokenizing.
     */
    private int inLineOffset = 1;

    /**
     * Buffer holding the characters of the current token.
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Acceptor consumer consuming any finished token.
     */
    private final Consumer<Token> acceptor;

    /**
     * Function used to look ahead some characters.
     */
    private final RethrowingFunction<Integer, Integer, TokenizeException> lookAheadFunction;

    /**
     * Ignore counter used to ignore character reads.
     */
    private int ignoreCounter = 0;

    public TokenizingContext(
            Consumer<Token> acceptor,
            RethrowingFunction<Integer, Integer, TokenizeException> lookAheadFunction
    ) {
        this.acceptor = acceptor;
        this.lookAheadFunction = lookAheadFunction;
    }

    public void incLineNum() {
        lineNum++;
        resetInLineOffset();
    }

    private void incInLineOffset() {
        inLineOffset++;
    }

    private void resetInLineOffset() {
        inLineOffset = 1;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getInLineOffset() {
        return inLineOffset;
    }

    /**
     * Look ahead some characters.
     * For example when passing relativePos=1 you will receive one character ahead of the current one.
     *
     * @param relativePos to get character for
     * @return the look ahead character at the given relative position or -1 if end of text reached
     */
    public int lookAhead(int relativePos) throws TokenizeException {
        return lookAheadFunction.apply(relativePos);
    }

    /**
     * Ignore the next few characters that are read.
     * Can be used in conjunction with the lookAhead function.
     *
     * @param count to ignore
     */
    public void ignoreNext(int count) {
        ignoreCounter = count;
    }

    public int getIgnoreCounter() {
        return ignoreCounter;
    }

    public void decrementIgnoreCounter() {
        ignoreCounter--;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    /**
     * Increase the end position.
     */
    public void incEndPos() {
        endPos++;
        incInLineOffset();
    }

    /**
     * Add the passed character to the value buffer.
     *
     * @param c to buffer
     */
    public void buffer(char c) {
        buffer.append(c);
    }

    /**
     * Get the current amount of buffered characters.
     *
     * @return length
     */
    public int bufferLength() {
        return buffer.length();
    }

    /**
     * Accept the current token.
     *
     * @param tokenGenerator generating a token
     */
    public void acceptToken(Function<String, Token> tokenGenerator) {
        String value = readValueAndReset();

        Token token = tokenGenerator.apply(value);
        if (!value.isEmpty()) {
            acceptor.accept(token);
        }

        setStartPos(token.getRange().getEnd());
    }

    /**
     * Read the current token value and reset the buffer.
     *
     * @return token value
     */
    public String readValueAndReset() {
        var val = buffer.toString();

        buffer.setLength(0);

        return val;
    }

}
