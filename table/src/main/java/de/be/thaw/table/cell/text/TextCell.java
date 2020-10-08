package de.be.thaw.table.cell.text;

import de.be.thaw.table.cell.Cell;
import de.be.thaw.table.cell.CellSpan;

/**
 * A simple cell holding text.
 */
public class TextCell implements Cell {

    /**
     * Text of the cell.
     */
    private final String text;

    /**
     * The current cell span of the cell.
     */
    private CellSpan span;

    public TextCell(String text) {
        this.text = text;
    }

    @Override
    public CellSpan getSpan() {
        return span;
    }

    @Override
    public void setSpan(CellSpan span) {
        this.span = span;
    }

    @Override
    public String toString() {
        return text;
    }

}
