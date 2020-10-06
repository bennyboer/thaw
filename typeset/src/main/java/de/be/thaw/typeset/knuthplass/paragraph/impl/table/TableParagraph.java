package de.be.thaw.typeset.knuthplass.paragraph.impl.table;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.table.Table;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;

/**
 * Paragraph representing a table.
 */
public class TableParagraph extends AbstractParagraph {

    /**
     * The actual table.
     */
    private final Table<ThawTableCell> table;

    public TableParagraph(Table<ThawTableCell> table, double lineWidth, DocumentNode node) {
        super(lineWidth, node);

        this.table = table;
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.TABLE;
    }

    /**
     * Get the table.
     *
     * @return table
     */
    public Table<ThawTableCell> getTable() {
        return table;
    }

}
