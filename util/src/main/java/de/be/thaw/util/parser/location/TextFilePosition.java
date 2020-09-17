package de.be.thaw.util.parser.location;

/**
 * Representation of a position in a text file.
 */
public class TextFilePosition {

    /**
     * The line of the position (starting from 1).
     */
    private final int line;

    /**
     * Position in the line (starting from 1).
     */
    private final int position;

    public TextFilePosition(int line, int position) {
        this.line = line;
        this.position = position;
    }

    /**
     * Get the line of the position in the text file (starting from 1).
     *
     * @return line
     */
    public int getLine() {
        return line;
    }

    /**
     * Get the position in the line (starting from 1).
     *
     * @return position
     */
    public int getPosition() {
        return position;
    }

}
