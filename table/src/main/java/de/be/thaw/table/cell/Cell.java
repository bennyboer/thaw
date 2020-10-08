package de.be.thaw.table.cell;

/**
 * Representation of a tables cell.
 */
public interface Cell {

    /**
     * Get the span of the cell.
     * The cell span defined how many rows and columns the cell takes up.
     *
     * @return span
     */
    CellSpan getSpan();

    /**
     * Set the cell span of the cell.
     *
     * @param span to set
     */
    void setSpan(CellSpan span);

}
