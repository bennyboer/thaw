package de.be.thaw.table.reader;

import de.be.thaw.table.Table;
import de.be.thaw.table.cell.Cell;
import de.be.thaw.table.reader.convert.CellConverter;
import de.be.thaw.table.reader.exception.TableReadException;

import java.io.Reader;

/**
 * Reader for tables.
 */
public interface TableReader<C extends Cell> {

    /**
     * Read a table from the given reader as source.
     *
     * @param reader    to read the table content from
     * @param converter to convert cell values to their proper cell types
     * @return the read table
     * @throws TableReadException in case the table could not be read
     */
    Table<C> read(Reader reader, CellConverter<C> converter) throws TableReadException;

}
