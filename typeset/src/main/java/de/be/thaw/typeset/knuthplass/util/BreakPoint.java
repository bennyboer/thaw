package de.be.thaw.typeset.knuthplass.util;

/**
 * Representation of a possible break point in a paragraph.
 * Essentially this means any place in a paragraph that we are allowed
 * to break the line or page at.
 */
public class BreakPoint {

    /**
     * Index of the break point in a paragraphs item list.
     */
    private final int index;

    /**
     * Penalty of breaking at this break point.
     */
    private final double penalty;

    public BreakPoint(int index, double penalty) {
        this.index = index;
        this.penalty = penalty;
    }

    /**
     * Get the index of the break point in a paragraphs item list.
     *
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the penalty for breaking at this point.
     *
     * @return penalty
     */
    public double getPenalty() {
        return penalty;
    }

}
