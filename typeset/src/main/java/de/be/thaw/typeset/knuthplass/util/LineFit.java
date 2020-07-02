package de.be.thaw.typeset.knuthplass.util;

/**
 * Enumeration describing the fitness of a line.
 */
public enum LineFit {

    TIGHT(0),
    NORMAL(1),
    LOOSE(2),
    VERY_LOOSE(3);

    /**
     * Class number of the line fit.
     */
    private final int fitnessClass;

    LineFit(int fitnessClass) {
        this.fitnessClass = fitnessClass;
    }

    /**
     * Get the class number of the line fit.
     *
     * @return class number
     */
    public int getFitnessClass() {
        return fitnessClass;
    }

}
