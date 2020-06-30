package de.be.thaw.core.typesetting.knuthplass.config.util.hyphen;

/**
 * Part of a hyphenated word.
 */
public class HyphenatedWordPart {

    /**
     * The default penalty.
     */
    public static final double DEFAULT_PENALTY = 50.0;

    /**
     * Part of the word that may end with a hyphen when breaking the line at this point.
     */
    private final String part;

    /**
     * Penalty to break the line for this part.
     */
    private final double penalty;

    /**
     * Create word part with the default penalty.
     *
     * @param part of the word
     */
    public HyphenatedWordPart(String part) {
        this.part = part;
        this.penalty = DEFAULT_PENALTY;
    }

    public HyphenatedWordPart(String part, double penalty) {
        this.part = part;
        this.penalty = penalty;
    }

    /**
     * Get the part of the hyphenated word.
     *
     * @return part
     */
    public String getPart() {
        return part;
    }

    /**
     * Get the penalty to break the line for this part.
     *
     * @return penalty
     */
    public double getPenalty() {
        return penalty;
    }

}
