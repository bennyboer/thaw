package de.be.thaw.table.reader.convert;

import de.be.thaw.table.cell.Cell;

/**
 * Converter to convert a cell value into a cell.
 */
public interface CellConverter<C extends Cell> {

    /**
     * Convert the passed value to a cell.
     *
     * @param value to convert
     * @return cell
     */
    C convert(String value);

}
