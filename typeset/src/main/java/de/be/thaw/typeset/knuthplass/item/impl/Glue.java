package de.be.thaw.typeset.knuthplass.item.impl;

import de.be.thaw.typeset.knuthplass.item.AbstractItem;
import de.be.thaw.typeset.knuthplass.item.ItemType;

/**
 * A glue is blank space that is able to vary in size (width).
 */
public class Glue extends AbstractItem {

    /**
     * Ideal or normal width of the item.
     */
    private final double width;

    /**
     * Stretchability of the items width.
     * When the normal width is too small it will stretch proportionally to this value.
     */
    private final double stretchability;

    /**
     * Shrinkability of the items width.
     * When the normal width is too big it will shrink proportionally to this value.
     */
    private final double shrinkability;

    public Glue(double width, double stretchability, double shrinkability) {
        this.width = width;
        this.stretchability = stretchability;
        this.shrinkability = shrinkability;
    }

    @Override
    public ItemType getType() {
        return ItemType.GLUE;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getStretchability() {
        return stretchability;
    }

    @Override
    public double getShrinkability() {
        return shrinkability;
    }

}
