package de.be.thaw.table.reader.csv;

import de.be.thaw.table.Table;
import de.be.thaw.table.cell.Cell;
import de.be.thaw.table.cell.CellSpan;
import de.be.thaw.table.impl.DefaultTable;
import de.be.thaw.table.reader.TableReader;
import de.be.thaw.table.reader.convert.CellConverter;
import de.be.thaw.table.reader.exception.TableReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reader for a CSV table.
 *
 * @param <C> the concrete cell type to fill a table with
 */
public class CSVTableReader<C extends Cell> implements TableReader<C> {

    /**
     * The CSV separator to use.
     */
    private final String separator;

    /**
     * The default row size to load a table with.
     */
    private final double defaultRowSize;

    /**
     * The total table width to determine column sizes with.
     */
    private final double totalTableWidth;

    public CSVTableReader(String separator, double defaultRowSize, double totalTableWidth) {
        this.separator = separator;

        this.defaultRowSize = defaultRowSize;
        this.totalTableWidth = totalTableWidth;
    }

    @Override
    public Table<C> read(Reader reader, CellConverter<C> converter) throws TableReadException {
        List<C> cells = new ArrayList<>();

        int row = 1;
        int anticipatedColumns = -1;
        double defaultColumnSize = 0;
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (anticipatedColumns == -1) {
                    // Could occurrences of the separator
                    anticipatedColumns = 1; // Always one more
                    int idx = -1;
                    do {
                        idx = line.indexOf(separator, idx + 1);
                        if (idx != -1) {
                            anticipatedColumns++;
                        }
                    } while (idx != -1);
                }

                String[] parts = line.split(Pattern.quote(separator), anticipatedColumns);
                if (row == 1) {
                    defaultColumnSize = totalTableWidth / parts.length;
                }

                int column = 1;

                for (String part : parts) {
                    C cell = converter.convert(part.trim());
                    cell.setSpan(new CellSpan(row, column));

                    cells.add(cell);

                    column++;
                }

                row++;
            }
        } catch (IOException e) {
            throw new TableReadException(e);
        }

        return new DefaultTable<>(cells, defaultRowSize, defaultColumnSize);
    }

}
