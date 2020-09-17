package de.be.thaw.util.parser.location;

/**
 * Class representing a text range in a text file.
 */
public class TextFileRange {

    /**
     * Start position of the range.
     */
    private final TextFilePosition start;

    /**
     * End position of the range.
     */
    private final TextFilePosition end;

    public TextFileRange(TextFilePosition start, TextFilePosition end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the start position of the range.
     *
     * @return start position
     */
    public TextFilePosition getStart() {
        return start;
    }

    /**
     * Get the end position of the range.
     *
     * @return end position
     */
    public TextFilePosition getEnd() {
        return end;
    }

}
