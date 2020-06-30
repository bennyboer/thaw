package de.be.thaw.core.typesetting.knuthplass.item;

/**
 * Item representation that defined default values for most attributes.
 */
public abstract class AbstractItem implements Item {

    @Override
    public double getStretchability() {
        return 0;
    }

    @Override
    public double getShrinkability() {
        return 0;
    }

    @Override
    public double getPenalty() {
        return 0;
    }

    @Override
    public boolean isFlagged() {
        return false;
    }

}
