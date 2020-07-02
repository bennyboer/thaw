package de.be.thaw.typeset.knuthplass.item.impl;

import de.be.thaw.typeset.knuthplass.item.AbstractItem;
import de.be.thaw.typeset.knuthplass.item.ItemType;

/**
 * A penalty specified a potential line breaking possibility
 * where its value means the aesthetic cost (low -> good, high -> bad).
 */
public class Penalty extends AbstractItem {

    /**
     * The maximum penalty.
     * All penalties greater or equal to this are treated as impossible line breaks.
     */
    public static final double MAX_PENALTY = 1000.0;

    /**
     * The minimum penalty.
     * All penalties lower or equal to this are treated as mandatory line breaks.
     */
    public static final double MIN_PENALTY = -1000.0;

    /**
     * Penalty to line break at this point.
     */
    private double penalty;

    /**
     * Width added to the line when breaking at this point.
     * For example the length of a hyphen when breaking here.
     */
    private final double width;

    /**
     * Whether the penalty is flagged.
     * The Knuth-Plass algorithm is trying to avoid two consecutive line breaks at
     * flagged penalties (for example two hyphenations in a row).
     */
    private final boolean flagged;

    public Penalty(double penalty, double width, boolean flagged) {
        this.penalty = penalty;
        this.width = width;
        this.flagged = flagged;
    }

    @Override
    public ItemType getType() {
        return ItemType.PENALTY;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getPenalty() {
        return penalty;
    }

    @Override
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Set the penalty.
     *
     * @param penalty to set
     */
    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    /**
     * Whether the line break at this point is mandatory.
     *
     * @return mandatory line break
     */
    public boolean isMandatoryLineBreak() {
        return penalty <= MIN_PENALTY;
    }

    /**
     * Whether the line break at this point is impossible.
     *
     * @return impossible line break
     */
    public boolean isImpossibleLineBreak() {
        return penalty >= MAX_PENALTY;
    }

}
