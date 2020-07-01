package de.be.thaw.typeset.knuthplass.item;

public interface Item {

    /**
     * Get the type of the item.
     *
     * @return type
     */
    ItemType getType();

    /**
     * Get the width of the item.
     *
     * @return width
     */
    double getWidth();

    /**
     * Get the stretchability of the items width.
     *
     * @return stretchability
     */
    double getStretchability();

    /**
     * Get the shrinkability of the items width.
     *
     * @return shrinkability
     */
    double getShrinkability();

    /**
     * Get the penalty to break at this position.
     *
     * @return penalty
     */
    double getPenalty();

    /**
     * Whether the item is flagged.
     *
     * @return flagged
     */
    boolean isFlagged();

}
