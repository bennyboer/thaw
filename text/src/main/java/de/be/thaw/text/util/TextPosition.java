package de.be.thaw.text.util;

/**
 * A text position representation.
 * Useful to give the user the exact position in the original text.
 */
public final class TextPosition {

    /**
     * Line number the token started in (starting with 1).
     */
    private final int startLine;

    /**
     * Line number the token ended in (starting with 1).
     */
    private final int endLine;

    /**
     * Start position (starting with 1) in the start line (inclusive).
     */
    private final int startPos;

    /**
     * End position (starting with 1) in the end line (inclusive).
     */
    private final int endPos;

    public TextPosition(int startLine, int startPos, int endLine, int endPos) {
        this.startLine = startLine;
        this.startPos = startPos;

        this.endLine = endLine;
        this.endPos = endPos;
    }

    /**
     * Get the starting line number.
     *
     * @return starting line number
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Get the ending line number.
     *
     * @return ending line number
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Get the start position (starting with 1) in the start line (inclusive).
     *
     * @return start position
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * Get the end position (starting with 1) in the end line (inclusive).
     *
     * @return end position
     */
    public int getEndPos() {
        return endPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextPosition that = (TextPosition) o;

        if (startLine != that.startLine) return false;
        if (endLine != that.endLine) return false;
        if (startPos != that.startPos) return false;
        return endPos == that.endPos;
    }

    @Override
    public int hashCode() {
        int result = startLine;
        result = 31 * result + endLine;
        result = 31 * result + startPos;
        result = 31 * result + endPos;
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%d:%d - %d:%d]", getStartLine(), getStartPos(), getEndLine(), getEndPos());
    }

}

