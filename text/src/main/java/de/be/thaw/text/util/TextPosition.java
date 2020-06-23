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
    private final int start;

    /**
     * End position (starting with 1) in the end line (exclusive).
     */
    private final int end;

    public TextPosition(int startLine, int endLine, int start, int end) {
        this.startLine = startLine;
        this.endLine = endLine;
        this.start = start;
        this.end = end;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getStartPos() {
        return start;
    }

    public int getEndPos() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextPosition that = (TextPosition) o;

        if (startLine != that.startLine) return false;
        if (endLine != that.endLine) return false;
        if (start != that.start) return false;
        return end == that.end;
    }

    @Override
    public int hashCode() {
        int result = startLine;
        result = 31 * result + endLine;
        result = 31 * result + start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%d:%d; %d:%d[", getStartLine(), getStartPos(), getEndLine(), getEndPos());
    }

}

