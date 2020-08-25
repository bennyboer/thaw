package de.be.thaw.font.util;

import de.be.thaw.util.Size;

/**
 * Size details of a character.
 */
public class CharacterSize extends Size {

    /**
     * The ascent of the character (space above the baseline).
     */
    private final double ascent;

    /**
     * The descent of the character (space below the baseline).
     */
    private final double descent;

    public CharacterSize(double width, double height, double ascent, double descent) {
        super(width, height);

        this.ascent = ascent;
        this.descent = descent;
    }

    public double getAscent() {
        return ascent;
    }

    public double getDescent() {
        return descent;
    }

}
