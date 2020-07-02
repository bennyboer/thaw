package de.be.thaw.typeset.knuthplass.util;

/**
 * Representation of an active break point.
 */
public class BreakPoint {

    /**
     * Index of the break point in a paragraphs item list.
     */
    private final int index;

    /**
     * Number of the line ending at this break point.
     */
    private int lineNumber = 0;

    /**
     * Fit of the line ending this break point.
     */
    private LineFit lineFit = LineFit.NORMAL;

    /**
     * The demerits of this break point.
     */
    private double demerits = 0;

    /**
     * The currently best previous break point.
     */
    private BreakPoint previous;

    public BreakPoint(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public LineFit getLineFit() {
        return lineFit;
    }

    public void setLineFit(LineFit lineFit) {
        this.lineFit = lineFit;
    }

    public double getDemerits() {
        return demerits;
    }

    public void setDemerits(double totalDemerits) {
        this.demerits = totalDemerits;
    }

    public BreakPoint getPrevious() {
        return previous;
    }

    public void setPrevious(BreakPoint previous) {
        this.previous = previous;
    }

}
