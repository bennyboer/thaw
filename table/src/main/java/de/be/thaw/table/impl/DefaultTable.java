package de.be.thaw.table.impl;

import de.be.thaw.table.Table;
import de.be.thaw.table.cell.Cell;
import de.be.thaw.table.cell.CellRange;
import de.be.thaw.table.cell.CellSpan;
import de.be.thaw.table.impl.exception.CouldNotMergeException;
import de.be.thaw.util.Bounds;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Default table implementation.
 *
 * @param <C> the concrete cell type
 */
public class DefaultTable<C extends Cell> implements Table<C> {

    /**
     * Special sizes of the rows.
     * For the default size consult property defaultRowSize;
     */
    private final Map<Integer, Double> rowSizeLookup = new HashMap<>();

    /**
     * The default size for each row.
     * Special sizes are held in the rowSizeLookup.
     */
    private double defaultRowSize;

    /**
     * Special sizes of the columns.
     * For the default size consult property defaultColumnSize;
     */
    private final Map<Integer, Double> columnSizeLookup = new HashMap<>();

    /**
     * The default size for each column.
     * Special sizes are held in the columnSizeLookup.
     */
    private double defaultColumnSize;

    /**
     * Current width of the table.
     */
    private double width;

    /**
     * Current height of the table.
     */
    private double height;

    /**
     * List of offsets for each row.
     * For example rowOffsets.get(2) would get the current offset of the second row.
     * rowOffsets.get(0) would always get you 0.
     */
    private final List<Double> rowOffsets;

    /**
     * List of offsets for each column.
     * For example columnOffsets.get(2) would get the current offset of the third column.
     * columnOffsets.get(0) would always get you 0.
     */
    private final List<Double> columnOffsets;

    /**
     * Lookup for cells at each possible position in the table.
     */
    private final List<List<C>> cellLookup;

    /**
     * Create a default table using the provided cells.
     *
     * @param cells             to show in the table
     * @param defaultRowSize    the default row size to use
     * @param defaultColumnSize the default column size to use
     */
    public DefaultTable(List<C> cells, double defaultRowSize, double defaultColumnSize) {
        this(cells, defaultRowSize, defaultColumnSize, row -> null, column -> null);
    }

    /**
     * Create a default table using the provided cells.
     *
     * @param cells                     to show in the table
     * @param defaultRowSize            the default row size
     * @param defaultColumnSize         the default column size
     * @param specialRowSizeSupplier    supplier for special row sizes
     * @param specialColumnSizeSupplier supplier for special column sizes
     */
    public DefaultTable(
            List<C> cells,
            double defaultRowSize,
            double defaultColumnSize,
            Function<Integer, Double> specialRowSizeSupplier,
            Function<Integer, Double> specialColumnSizeSupplier
    ) {
        if (cells.isEmpty()) {
            throw new IllegalArgumentException("A DefaultTable must always at least consist of a single cell");
        }

        this.defaultRowSize = defaultRowSize;
        this.defaultColumnSize = defaultColumnSize;

        // Find the dimensions (rows and columns) of the table
        int maxRow = 1;
        int maxColumn = 1;
        for (C cell : cells) {
            maxRow = Math.max(maxRow, cell.getSpan().getRowRange().getEnd());
            maxColumn = Math.max(maxColumn, cell.getSpan().getColumnRange().getEnd());
        }

        // Fill special row and column sizes
        for (int row = 1; row <= maxRow; row++) {
            Double specialSize = specialRowSizeSupplier.apply(row);
            if (specialSize != null) {
                rowSizeLookup.put(row, specialSize);
            }
        }
        for (int column = 1; column <= maxColumn; column++) {
            Double specialSize = specialColumnSizeSupplier.apply(column);
            if (specialSize != null) {
                columnSizeLookup.put(column, specialSize);
            }
        }

        // Create lookup collections
        rowOffsets = new ArrayList<>(maxRow);
        columnOffsets = new ArrayList<>(maxColumn);
        cellLookup = new ArrayList<>(maxRow);
        for (int row = 1; row <= maxRow; row++) {
            List<C> cellList = new ArrayList<>(maxColumn);

            for (int column = 1; column <= maxColumn; column++) {
                cellList.add(null);
            }

            cellLookup.add(cellList);
        }

        // Fill offset lookups
        double rowOffset = 0;
        for (int row = 1; row <= maxRow; row++) {
            rowOffsets.add(rowOffset);
            rowOffset += getRowSize(row);
        }
        height = rowOffset;

        double columnOffset = 0;
        for (int column = 1; column <= maxColumn; column++) {
            columnOffsets.add(columnOffset);
            columnOffset += getColumnSize(column);
        }
        width = columnOffset;

        // Fill cell lookup
        for (C cell : cells) {
            for (int row = cell.getSpan().getRowRange().getStart(); row <= cell.getSpan().getRowRange().getEnd(); row++) {
                List<C> rowLookup = cellLookup.get(row - 1);

                for (int column = cell.getSpan().getColumnRange().getStart(); column <= cell.getSpan().getColumnRange().getEnd(); column++) {
                    rowLookup.set(column - 1, cell);
                }
            }
        }
    }

    @Override
    public Size getSize() {
        return new Size(width, height);
    }

    @Override
    public int getRows() {
        return rowOffsets.size();
    }

    @Override
    public int getColumns() {
        return columnOffsets.size();
    }

    @Override
    public double getRowSize(int row) {
        return rowSizeLookup.getOrDefault(row, defaultRowSize);
    }

    /**
     * Set the row size of the given row.
     *
     * @param row  to set size of
     * @param size to set
     */
    public void setRowSize(int row, double size) {
        double sizeDiff = size - getRowSize(row);

        rowSizeLookup.put(row, size);

        // We need to update the row offsets lookup for every row following the modified row
        for (int r = row + 1; r <= getRows(); r++) {
            rowOffsets.set(r - 1, rowOffsets.get(r - 1) + sizeDiff);
        }
    }

    @Override
    public double getColumnSize(int column) {
        return columnSizeLookup.getOrDefault(column, defaultColumnSize);
    }

    /**
     * Set the column size of the given row.
     *
     * @param column to set size of
     * @param size   to set
     */
    public void setColumnSize(int column, double size) {
        double sizeDiff = size - getColumnSize(column);

        columnSizeLookup.put(column, size);

        // We need to update the column offsets lookup for every column following the modified column
        for (int c = column + 1; c <= getColumns(); c++) {
            columnOffsets.set(c - 1, columnOffsets.get(c - 1) + sizeDiff);
        }
    }

    @Override
    public Bounds getBounds(CellSpan span) {
        double x = columnOffsets.get(span.getColumnRange().getStart() - 1);
        double y = rowOffsets.get(span.getRowRange().getStart() - 1);

        double spanWidth = columnOffsets.get(span.getColumnRange().getEnd() - 1) - x + getColumnSize(span.getColumnRange().getEnd());
        double spanHeight = rowOffsets.get(span.getRowRange().getEnd() - 1) - y + getRowSize(span.getRowRange().getEnd());

        return new Bounds(
                new Position(x, y),
                new Size(spanWidth, spanHeight)
        );
    }

    @Override
    public Optional<C> getCell(int row, int column) {
        return Optional.ofNullable(cellLookup.get(row - 1)
                .get(column - 1));
    }

    @Override
    public List<C> getCells(CellSpan span) {
        List<C> result = new ArrayList<>();

        Set<CellSpan> seenSpans = new HashSet<>(); // Set of already seen spans which should not be added to the result
        for (int row = span.getRowRange().getStart(); row <= span.getRowRange().getEnd(); row++) {
            for (int column = span.getColumnRange().getStart(); column <= span.getColumnRange().getEnd(); column++) {
                getCell(row, column).ifPresent(cell -> {
                    if (!seenSpans.contains(cell.getSpan())) {
                        seenSpans.add(cell.getSpan());

                        result.add(cell);
                    }
                });
            }
        }

        return result;
    }

    @Override
    public List<C> getCells() {
        return getCells(new CellSpan(
                new CellRange(1, getRows()),
                new CellRange(1, getColumns())
        ));
    }

    /**
     * Merge the provided cell span to one cell.
     *
     * @param span to merge
     * @throws CouldNotMergeException in case the cell span to merge already contains (or cuts) merged cells
     */
    public void merge(CellSpan span) throws CouldNotMergeException {
        // Fetch the cell to take over the new cell span
        C firstCell = getCell(span.getRowRange().getStart(), span.getColumnRange().getStart())
                .orElseThrow(() -> new CouldNotMergeException(String.format(
                        "There is no cell at the first row (%d) and column (%d) of the given cell span to merge",
                        span.getRowRange().getStart(),
                        span.getColumnRange().getStart()
                )));

        // Check whether all cells can be merged
        for (int row = span.getRowRange().getStart(); row <= span.getRowRange().getEnd(); row++) {
            for (int column = span.getColumnRange().getStart(); column <= span.getColumnRange().getEnd(); column++) {
                C cell = getCell(row, column).orElse(null);

                if (cell != null && !cell.getSpan().isSingleCellSpan()) {
                    throw new CouldNotMergeException(String.format(
                            "Cell at row %d and column %d is already merged",
                            row,
                            column
                    ));
                }
            }
        }

        // Update span of the cell
        firstCell.setSpan(span);

        // Update cell lookup
        for (int row = span.getRowRange().getStart(); row <= span.getRowRange().getEnd(); row++) {
            for (int column = span.getColumnRange().getStart(); column <= span.getColumnRange().getEnd(); column++) {
                cellLookup.get(row - 1).set(column - 1, firstCell);
            }
        }
    }

    /**
     * Split all cells in (or cutting) the provided span that span more than one cell.
     *
     * @param span to split all cells of
     */
    public void split(CellSpan span) {
        // Search cells that span multiple cells in the given cell span
        Map<CellSpan, C> foundMultiCellSpans = new HashMap<>();
        for (int row = span.getRowRange().getStart(); row <= span.getRowRange().getEnd(); row++) {
            for (int column = span.getColumnRange().getStart(); column <= span.getColumnRange().getEnd(); column++) {
                getCell(row, column).ifPresent(cell -> {
                    if (!foundMultiCellSpans.containsKey(cell.getSpan())) {
                        foundMultiCellSpans.put(cell.getSpan(), cell);
                    }
                });
            }
        }

        // Split all found cell spans
        for (Map.Entry<CellSpan, C> entry : foundMultiCellSpans.entrySet()) {
            CellSpan s = entry.getKey();

            // Update span
            entry.getValue().setSpan(new CellSpan(
                    s.getRowRange().getStart(),
                    s.getColumnRange().getStart()
            ));

            // Update cell lookup
            for (int row = s.getRowRange().getStart(); row <= s.getRowRange().getEnd(); row++) {
                for (int column = s.getColumnRange().getStart(); column <= s.getColumnRange().getEnd(); column++) {
                    if (row != s.getRowRange().getStart() || column != s.getColumnRange().getStart()) {
                        cellLookup.get(row - 1).set(column - 1, null);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int row = 1; row <= getRows(); row++) {
            sb.append("| ");

            for (int column = 1; column <= getColumns(); column++) {
                sb.append(getCell(row, column)
                        .map(Cell::toString)
                        .orElse(" "));

                sb.append(" |");
                if (column != getColumns()) {
                    sb.append(' ');
                }
            }

            if (row != getRows()) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

}
