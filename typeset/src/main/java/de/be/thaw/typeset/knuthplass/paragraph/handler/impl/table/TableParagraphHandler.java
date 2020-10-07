package de.be.thaw.typeset.knuthplass.paragraph.handler.impl.table;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.table.Table;
import de.be.thaw.table.cell.CellRange;
import de.be.thaw.table.cell.CellSpan;
import de.be.thaw.table.impl.DefaultTable;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.table.TableParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.table.ThawTableCell;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.util.Bounds;
import de.be.thaw.util.Position;
import de.be.thaw.util.unit.Unit;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handler for table paragraphs.
 */
public class TableParagraphHandler implements ParagraphTypesetHandler {

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.TABLE;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        TableParagraph tableParagraph = (TableParagraph) paragraph;

        // TODO deal with margins paddings on the whole table (how do we set then in the styles?)

        Table<ThawTableCell> table = tableParagraph.getTable();

        // Fetch rows with specially set (fixed) row sizes
        Set<Integer> fixedSizedRows = new HashSet<>();
        for (int row = 1; row <= table.getRows(); row++) {
            if (table.getRowSize(row) != ((DefaultTable<ThawTableCell>) table).getDefaultRowSize()) {
                fixedSizedRows.add(row);
            }
        }

        for (ThawTableCell cell : table.getCells()) {
            double actualCellHeight = typeSetCell(cell, table.getBounds(cell.getSpan()), ctx);

            // Only set a new row size if the current row size is not specially set (fixed size)
            if (!fixedSizedRows.contains(cell.getSpan().getRowRange().getEnd())) {
                double neededRowHeight = actualCellHeight;
                if (cell.getSpan().getRowRange().getStart() != cell.getSpan().getRowRange().getEnd()) {
                    // Cell spans over multiple rows -> only change the last row size
                    neededRowHeight = actualCellHeight - table.getBounds(new CellSpan(
                            new CellRange(cell.getSpan().getRowRange().getStart(), cell.getSpan().getRowRange().getEnd() - 1),
                            cell.getSpan().getColumnRange()
                    )).getSize().getHeight();
                }

                if (table.getRowSize(cell.getSpan().getRowRange().getEnd()) < neededRowHeight) {
                    ((DefaultTable<ThawTableCell>) table).setRowSize(cell.getSpan().getRowRange().getEnd(), neededRowHeight);
                }
            }
        }

        ctx.getPositionContext().increaseY(table.getSize().getHeight());
    }

    /**
     * Typeset the passed cell.
     *
     * @param cell       to typeset contents of
     * @param cellBounds bounds of the cell
     * @param ctx        the typesetting context
     * @return the actual height of the typeset cell
     * @throws TypeSettingException in case the cell could not be typeset
     */
    private double typeSetCell(ThawTableCell cell, Bounds cellBounds, TypeSettingContext ctx) throws TypeSettingException {
        // Create style model for the cell
        StyleModel styleModel = new DefaultStyleModel();
        styleModel.addBlock(new StyleBlock(
                new StyleSelectorBuilder().setTargetName("document").build(),
                Map.ofEntries(
                        Map.entry(StyleType.FIRST_LINE_INDENT, new DoubleStyleValue(0.0, Unit.MILLIMETER))
                )
        ));

        // Typeset cell
        List<Page> pages = ctx.typesetThawTextFormat(cell.toString(), cellBounds.getSize().getWidth(), styleModel);
        Page page = pages.get(0);

        // Re-layout elements
        double cellHeight = 0;
        for (Element element : page.getElements()) {
            cellHeight = Math.max(cellHeight, element.getPosition().getY() + element.getSize().getHeight());

            AbstractElement abstractElement = (AbstractElement) element;
            abstractElement.setPosition(new Position(
                    abstractElement.getPosition().getX() + cellBounds.getPosition().getX() + ctx.getPositionContext().getX(),
                    abstractElement.getPosition().getY() + cellBounds.getPosition().getY() + ctx.getPositionContext().getY()
            ));
        }

        ctx.getCurrentPageElements().addAll(page.getElements());

        return cellHeight;
    }

}
