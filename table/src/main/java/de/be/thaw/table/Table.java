package de.be.thaw.table;

import de.be.thaw.table.cell.Cell;
import de.be.thaw.table.cell.CellSpan;
import de.be.thaw.util.Bounds;
import de.be.thaw.util.Size;

import java.util.List;
import java.util.Optional;

/**
 * Representation of a table.
 *
 * @param <C> concrete cell type
 */
public interface Table<C extends Cell> {

    /**
     * Get the total size of the table.
     *
     * @return size
     */
    Size getSize();

    /**
     * Get the number of rows of the table.
     *
     * @return rows
     */
    int getRows();

    /**
     * Get the number of columns of the table.
     *
     * @return columns
     */
    int getColumns();

    /**
     * Get a rows size.
     *
     * @param row to get size of
     * @return row size
     */
    double getRowSize(int row);

    /**
     * Get a columns size.
     *
     * @param column to get size of
     * @return column size
     */
    double getColumnSize(int column);

    /**
     * Get the bounds for the passed cell span.
     *
     * @param span to get bounds for
     * @return bounds
     */
    Bounds getBounds(CellSpan span);

    /**
     * Get a cell at the passed row and column.
     *
     * @param row    to get cell at
     * @param column to get cell at
     * @return cell (or empty optional if there is no cell at the given position)
     */
    Optional<C> getCell(int row, int column);

    /**
     * Get all cells in (or cutting) the provided span.
     * Note that the cells do not have to be fully contained in the span.
     *
     * @param span to get cells in
     * @return array of cells in (or cutting) the given span
     */
    List<C> getCells(CellSpan span);

    /**
     * Get all cells in the table.
     *
     * @return cells
     */
    List<C> getCells();

}
