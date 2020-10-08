package de.be.thaw.table.cell;

import java.util.Objects;

/**
 * Representation of a span of cells.
 * A span in contrast to a range is a 2-dimensional selector for cells.
 * That means you can select a whole matrix of cells with multiple rows and columns.
 */
public class CellSpan {

    /**
     * Range of rows in the span.
     */
    private final CellRange rowRange;

    /**
     * Range of columns in the span.
     */
    private final CellRange columnRange;

    /**
     * Create a cell span spanning a single cell.
     *
     * @param row    to span over
     * @param column to span over
     */
    public CellSpan(int row, int column) {
        this(new CellRange(row), new CellRange(column));
    }

    public CellSpan(CellRange rowRange, CellRange columnRange) {
        this.rowRange = rowRange;
        this.columnRange = columnRange;
    }

    /**
     * Get the range of rows in the span.
     *
     * @return row range
     */
    public CellRange getRowRange() {
        return rowRange;
    }

    /**
     * Get the range of columns in the span.
     *
     * @return column range
     */
    public CellRange getColumnRange() {
        return columnRange;
    }

    /**
     * Check whether the cell span spans only a single cell.
     *
     * @return whether the span spans only a single cell
     */
    public boolean isSingleCellSpan() {
        return getRowRange().getStart() == getRowRange().getEnd()
                && getColumnRange().getStart() == getColumnRange().getEnd();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellSpan cellSpan = (CellSpan) o;

        if (!Objects.equals(rowRange, cellSpan.rowRange)) return false;
        return Objects.equals(columnRange, cellSpan.columnRange);
    }

    @Override
    public int hashCode() {
        int result = rowRange != null ? rowRange.hashCode() : 0;
        result = 31 * result + (columnRange != null ? columnRange.hashCode() : 0);
        return result;
    }

}
