package de.be.thaw.typeset.knuthplass.util;

/**
 * Representation of an active break point.
 */
public class ActiveBreakPoint {

    /**
     * The underlying break point.
     */
    private final BreakPoint breakPoint;

    // TODO Add all needed parameters

    public ActiveBreakPoint(BreakPoint breakPoint) {
        this.breakPoint = breakPoint;
    }

    /**
     * Get underlying break point.
     *
     * @return break point
     */
    public BreakPoint getBreakPoint() {
        return breakPoint;
    }

}
