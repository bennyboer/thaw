package de.be.thaw.text.tokenizer;

import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.tokenizer.util.RethrowingFunction;
import de.be.thaw.text.util.TextPosition;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Current tokenizing context used during tokenizing.
 */
public class TokenizingContext {

    /**
     * Number of the line the current token started in the original text.
     */
    private int startLine = 1;

    /**
     * Number of the line currently tokenizing in the original text..
     */
    private int currentLine = 1;

    /**
     * Offset in line where currently tokenizing in the original text.
     */
    private int currentPos = 0;

    /**
     * Start offset in the starting line of the current token in the original text.
     */
    private int startPos = 1;

    /**
     * Length of the last line.
     */
    private int lastLineLength = -1;

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

    /**
     * Increase the current line number.
     */
    public void increaseLineNumber() {
        currentLine++;
        lastLineLength = currentPos;
        currentPos = 0; // Resetting line offset
    }

    /**
     * Increase the current position where currently at in the original text.
     */
    public void increaseCurrentPos() {
        currentPos++;
    }

    /**
     * Get the current line number.
     *
     * @return current line number
     */
    public int getCurrentLine() {
        return currentLine;
    }

    /**
     * Get the current position in the current line in the original text.
     *
     * @return current position
     */
    public int getCurrentPos() {
        return currentPos;
    }

    /**
     * Get the starting line in the original text of the current token.
     *
     * @return starting line number
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Get the start position in the starting line.
     *
     * @return start position
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * Get the buffer.
     * Used to buffer characters for a token that
     * has not yet been accepted.
     *
     * @return buffer
     */
    public StringBuilder getBuffer() {
        return buffer;
    }

    /**
     * Read the current token value and reset the buffer.
     *
     * @return token value
     */
    public String readBufferAndReset() {
        var val = buffer.toString();

        buffer.setLength(0);

        return val;
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

    /**
     * Get the current ignore counter.
     *
     * @return ignore counter
     */
    public int getIgnoreCounter() {
        return ignoreCounter;
    }

    /**
     * Decrement the ignore counter by one.
     */
    public void decrementIgnoreCounter() {
        ignoreCounter--;
    }

    /**
     * Set the current line.
     *
     * @param currentLine current line
     */
    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    /**
     * Set the current position.
     *
     * @param currentPos current position.
     */
    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    /**
     * Set the starting line  number.
     *
     * @param startLine starting line number
     */
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    /**
     * Set the starting position.
     *
     * @param startPos starting position.
     */
    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    /**
     * Get the length of the last line.
     *
     * @return length of the last line
     */
    public int getLastLineLength() {
        return lastLineLength;
    }

    /**
     * Accept a token.
     *
     * @param token to accept
     */
    public void accept(Token token) {
        // Only accepting non-empty tokens
        if (!token.getValue().isEmpty()) {
            acceptor.accept(token);
        }

        startLine = token.getPosition().getEndLine();
        startPos = token.getPosition().getEndPos() + 1; // Increase by one since the end pos is inclusive!
    }

    /**
     * Accept a token using a token generator.
     *
     * @param tokenGenerator to generate the token to accept
     */
    public void accept(BiFunction<String, TextPosition, Token> tokenGenerator) {
        String value = readBufferAndReset();
        TextPosition position = new TextPosition(getStartLine(), getStartPos(), getCurrentLine(), getCurrentPos());

        Token token = tokenGenerator.apply(value, position);
        accept(token);
    }

}
