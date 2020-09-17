package de.be.thaw.style.parser.lexer.context;

import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.state.RootState;
import de.be.thaw.style.parser.lexer.state.SFLexerState;
import de.be.thaw.style.parser.lexer.token.StyleFormatToken;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;
import de.be.thaw.util.parser.location.TextFilePosition;
import de.be.thaw.util.parser.location.TextFileRange;

import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Context used during lexing a style format.
 */
public class SFLexerContext {

    /**
     * The current range context of the current token being processed.
     */
    private final RangeContext rangeCtx = new RangeContext();

    /**
     * The state stack.
     */
    private final Stack<SFLexerState> stateStack = new Stack<>();

    /**
     * Buffer for already processed characters for the current token.
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Consumer used to publish accepted tokens.
     */
    private final Consumer<StyleFormatToken> tokenConsumer;

    /**
     * Function used to look ahead some characters.
     */
    private final Function<Integer, Optional<Character>> lookAheadFunction;

    /**
     * Whether the current state is marked to be popped after accepting the currently processing character.
     */
    private boolean stateMarkedForPop = false;

    public SFLexerContext(Consumer<StyleFormatToken> tokenConsumer, Function<Integer, Optional<Character>> lookAheadFunction) {
        this.tokenConsumer = tokenConsumer;
        this.lookAheadFunction = lookAheadFunction;

        // Add the start state to the stack
        stateStack.push(new RootState());
    }

    /**
     * Look ahead the passed offset of characters from the current position.
     *
     * @param offset to look ahead
     * @return the character at the offset of an empty Optional
     */
    public Optional<Character> lookAhead(int offset) {
        return lookAheadFunction.apply(offset);
    }

    /**
     * Push a state on the lexer stack.
     *
     * @param state to push
     */
    public void pushState(SFLexerState state) {
        acceptDanglingToken();
        stateStack.push(state);
    }

    /**
     * Pop a state on the lexer stack.
     * This should involve accepting a token.
     *
     * @throws StyleFormatLexerException in case the state could not be popped
     */
    public void popState() throws StyleFormatLexerException {
        acceptDanglingToken();

        if (stateStack.size() == 1) {
            throw new StyleFormatLexerException("Trying to pop the last state from the stack which is illegal!");
        }
        stateStack.pop();
    }

    /**
     * Mark the current state to be popped after accepting the current character to be included in the current token.
     */
    public void popStateAfter() {
        stateMarkedForPop = true;
    }

    /**
     * Filter characters before processing them.
     *
     * @param c to filter
     * @return the filtered character (-1 if not to process entirely!)
     */
    private int filterChar(char c) {
        return switch (c) {
            case '\t' -> ' '; // Turn tab into a white space
            case '\r' -> -1; // Do not process
            default -> c;
        };
    }

    /**
     * Process the passed character.
     *
     * @param c to process
     */
    public void process(char c) throws StyleFormatLexerException {
        // Filter the character
        int filteredChar = filterChar(c);
        if (filteredChar == -1) {
            return;
        }
        c = (char) filteredChar;

        // Process the character in the current state
        stateStack.peek().process(c, this);

        // Buffer the character
        buffer.append(c);

        // Update text range context properly
        if (c == '\n') {
            rangeCtx.setEndLine(rangeCtx.getEndLine() + 1);
            rangeCtx.setEndPos(1);
        } else {
            rangeCtx.setEndPos(rangeCtx.getEndPos() + 1);
        }

        if (stateMarkedForPop) {
            stateMarkedForPop = false;
            popState();
        }
    }

    /**
     * Called when reading the style format source has finished.
     * We should publish the last dangling token (if any).
     */
    public void finish() {
        acceptDanglingToken();
    }

    /**
     * Accept a dangling token.
     * A token is dangling, when we have one or more
     * characters buffered which need to be turned into a token yet.
     */
    private void acceptDanglingToken() {
        if (hasDanglingToken()) {
            // Collect needed token attributes
            String value = readAndResetBuffer();
            TextFileRange range = readAndResetRangeCtx();
            StyleFormatTokenType type = stateStack.peek().getType();

            // Build token
            StyleFormatToken token = new StyleFormatToken(value, range, type);

            // Accept token
            tokenConsumer.accept(token);
        }
    }

    /**
     * Check whether we have a dangling token yet to be accepted.
     *
     * @return whether we have a dangling token
     */
    private boolean hasDanglingToken() {
        return buffer.length() > 0;
    }

    /**
     * Read and reset the character buffer.
     *
     * @return the contents of the buffer before reset
     */
    private String readAndResetBuffer() {
        String result = buffer.toString(); // Read the buffer
        buffer.setLength(0); // Reset the buffer

        return result;
    }

    /**
     * Read and reset the current range ctx.
     *
     * @return text range of the current dangling token
     */
    private TextFileRange readAndResetRangeCtx() {
        TextFileRange range = rangeCtx.toTextFileRange(); // Read range

        // Reset range ctx
        rangeCtx.setStartLine(rangeCtx.getEndLine());
        rangeCtx.setStartPos(rangeCtx.getEndPos());

        return range;
    }

    /**
     * Get the current position we are at.
     *
     * @return current position
     */
    public TextFilePosition getCurrentPosition() {
        return rangeCtx.toCurrentPosition();
    }

    /**
     * The current text range context.
     */
    private static class RangeContext {

        /**
         * Current tokens start line.
         */
        private int startLine = 1;

        /**
         * Current tokens start position in the start line.
         */
        private int startPos = 1;

        /**
         * Current tokens end line.
         */
        private int endLine = 1;

        /**
         * Current tokens end position in the end line.
         */
        private int endPos = 1;

        public int getStartLine() {
            return startLine;
        }

        public void setStartLine(int startLine) {
            this.startLine = startLine;
        }

        public int getStartPos() {
            return startPos;
        }

        public void setStartPos(int startPos) {
            this.startPos = startPos;
        }

        public int getEndLine() {
            return endLine;
        }

        public void setEndLine(int endLine) {
            this.endLine = endLine;
        }

        public int getEndPos() {
            return endPos;
        }

        public void setEndPos(int endPos) {
            this.endPos = endPos;
        }

        /**
         * Convert the current context to a text file range.
         *
         * @return text file range
         */
        public TextFileRange toTextFileRange() {
            return new TextFileRange(toStartPosition(), toCurrentPosition());
        }

        /**
         * Get the current start position of the current token.
         *
         * @return current start position
         */
        public TextFilePosition toStartPosition() {
            return new TextFilePosition(startLine, startPos);
        }

        /**
         * Get the current position of tokenizing/lexing.
         *
         * @return current position
         */
        public TextFilePosition toCurrentPosition() {
            return new TextFilePosition(endLine, endPos);
        }

    }

}
