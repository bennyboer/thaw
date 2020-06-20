package de.be.thaw.text.util;

/**
 * A text range representation.
 */
public final class TextRange {

    /**
     * Start index of the range (inclusive).
     */
    private final int start;

    /**
     * The end of the range (exclusive).
     */
    private final int end;

    public TextRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the start of the range (inclusive).
     *
     * @return start of the range
     */
    public int getStart() {
        return start;
    }

    /**
     * Get the end of the range (exclusive).
     *
     * @return end of the range
     */
    public int getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextRange textRange = (TextRange) o;

        if (start != textRange.start) return false;
        return end == textRange.end;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        return result;
    }

    @Override
    public String toString() {
        return String.format("[%d; %d[", start, end);
    }

}
