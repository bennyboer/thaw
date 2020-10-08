package de.be.thaw.table.cell;

/**
 * A range of cells (1-dimensional).
 */
public class CellRange {

    /**
     * Get the start of the range.
     */
    private final int start;

    /**
     * Get the end of the range.
     */
    private final int end;

    /**
     * Create a cell range only ranging over one cell.
     *
     * @param startAndEnd of the range
     */
    public CellRange(int startAndEnd) {
        this(startAndEnd, startAndEnd);
    }

    /**
     * Create a cell range ranging from the given start to the provided end.
     *
     * @param start of the range
     * @param end   of the range
     */
    public CellRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the start of the range.
     *
     * @return start
     */
    public int getStart() {
        return start;
    }

    /**
     * Get the end of the range.
     *
     * @return end
     */
    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("[%d - %d]", getStart(), getEnd());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellRange cellRange = (CellRange) o;

        if (start != cellRange.start) return false;
        return end == cellRange.end;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        return result;
    }

}
