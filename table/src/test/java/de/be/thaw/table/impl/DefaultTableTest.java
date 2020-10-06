package de.be.thaw.table.impl;

import de.be.thaw.table.cell.Cell;
import de.be.thaw.table.cell.CellRange;
import de.be.thaw.table.cell.CellSpan;
import de.be.thaw.table.impl.exception.CouldNotMergeException;
import de.be.thaw.util.Bounds;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultTableTest {

    @Test
    public void simpleTest() {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Arrays.asList(
                        new TestCell("X", new CellSpan(1, 1)),
                        new TestCell("A", new CellSpan(5, 4))
                ),
                25,
                100
        );

        Assertions.assertEquals(
                "| X |   |   |   |\n" +
                        "|   |   |   |   |\n" +
                        "|   |   |   |   |\n" +
                        "|   |   |   |   |\n" +
                        "|   |   |   | A |",
                table.toString()
        );
        Assertions.assertEquals(new Size(400, 125), table.getSize());
    }

    @Test
    public void noCellsExceptionTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DefaultTable(
                new ArrayList(),
                25,
                100
        ));
    }

    @Test
    public void mergeTest() throws CouldNotMergeException {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Arrays.asList(
                        new TestCell("X", new CellSpan(1, 1)),
                        new TestCell("A", new CellSpan(5, 4))
                ),
                25,
                100
        );

        Assertions.assertEquals(table.getBounds(table.getCell(1, 1)
                .orElseThrow()
                .getSpan()), new Bounds(new Position(0, 0), new Size(100, 25)));

        table.merge(new CellSpan(
                new CellRange(1, 3),
                new CellRange(1, 2)
        ));

        Assertions.assertEquals(
                "| X | X |   |   |\n" +
                        "| X | X |   |   |\n" +
                        "| X | X |   |   |\n" +
                        "|   |   |   |   |\n" +
                        "|   |   |   | A |",
                table.toString()
        );
        Assertions.assertEquals(table.getBounds(table.getCell(1, 1)
                .orElseThrow()
                .getSpan()), new Bounds(new Position(0, 0), new Size(200, 75)));
    }

    @Test
    public void splitTest() throws CouldNotMergeException {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Arrays.asList(
                        new TestCell("X", new CellSpan(1, 1)),
                        new TestCell("A", new CellSpan(5, 4))
                ),
                25,
                100
        );

        table.merge(new CellSpan(
                new CellRange(1, 3),
                new CellRange(1, 2)
        ));
        Assertions.assertEquals(table.getBounds(table.getCell(1, 1)
                .orElseThrow()
                .getSpan()), new Bounds(new Position(0, 0), new Size(200, 75)));

        table.split(new CellSpan(
                new CellRange(1, 3),
                new CellRange(1, 2)
        ));

        Assertions.assertEquals(
                "| X |   |   |   |\n" +
                        "|   |   |   |   |\n" +
                        "|   |   |   |   |\n" +
                        "|   |   |   |   |\n" +
                        "|   |   |   | A |",
                table.toString()
        );
        Assertions.assertEquals(table.getBounds(table.getCell(1, 1)
                .orElseThrow()
                .getSpan()), new Bounds(new Position(0, 0), new Size(100, 25)));
    }

    @Test
    public void couldNotMergeTest1() throws CouldNotMergeException {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Arrays.asList(
                        new TestCell("X", new CellSpan(1, 1)),
                        new TestCell("A", new CellSpan(5, 4))
                ),
                25,
                100
        );

        table.merge(new CellSpan(
                new CellRange(1, 3),
                new CellRange(1, 2)
        ));

        Assertions.assertThrows(CouldNotMergeException.class, () -> table.merge(new CellSpan(
                new CellRange(1, 4),
                new CellRange(1, 2)
        )));
    }

    @Test
    public void couldNotMergeTest2() {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Collections.singletonList(
                        new TestCell("A", new CellSpan(5, 4))
                ),
                25,
                100
        );

        Assertions.assertThrows(CouldNotMergeException.class, () -> table.merge(new CellSpan(
                new CellRange(1, 3),
                new CellRange(1, 2)
        )));
    }

    @Test
    public void setSpecificSizeTest() {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Arrays.asList(
                        new TestCell("X", new CellSpan(1, 1)),
                        new TestCell("A", new CellSpan(5, 4)),
                        new TestCell("D", new CellSpan(6, 6))
                ),
                25,
                100
        );

        Assertions.assertEquals(
                new Bounds(new Position(300, 100), new Size(100, 25)),
                table.getBounds(table.getCell(5, 4)
                        .orElseThrow()
                        .getSpan())
        );
        Assertions.assertEquals(
                new Bounds(new Position(500, 125), new Size(100, 25)),
                table.getBounds(table.getCell(6, 6)
                        .orElseThrow()
                        .getSpan())
        );

        table.setRowSize(5, 100);
        table.setColumnSize(4, 50);

        Assertions.assertEquals(
                new Bounds(new Position(300, 100), new Size(50, 100)),
                table.getBounds(table.getCell(5, 4)
                        .orElseThrow()
                        .getSpan())
        );
        Assertions.assertEquals(
                new Bounds(new Position(450, 200), new Size(100, 25)),
                table.getBounds(table.getCell(6, 6)
                        .orElseThrow()
                        .getSpan())
        );
    }

    @Test
    public void setSpecificSizeTest2() {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Arrays.asList(
                        new TestCell("X", new CellSpan(1, 1)),
                        new TestCell("A", new CellSpan(5, 4)),
                        new TestCell("D", new CellSpan(6, 6))
                ),
                25,
                100,
                row -> row == 5 ? 100.0 : null,
                column -> column == 4 ? 50.0 : null
        );

        Assertions.assertEquals(
                new Bounds(new Position(300, 100), new Size(50, 100)),
                table.getBounds(table.getCell(5, 4)
                        .orElseThrow()
                        .getSpan())
        );
        Assertions.assertEquals(
                new Bounds(new Position(450, 200), new Size(100, 25)),
                table.getBounds(table.getCell(6, 6)
                        .orElseThrow()
                        .getSpan())
        );
    }

    @Test
    public void getCellsTest() {
        DefaultTable<TestCell> table = new DefaultTable<>(
                Arrays.asList(
                        new TestCell("X", new CellSpan(1, 1)),
                        new TestCell("A", new CellSpan(5, 4)),
                        new TestCell("D", new CellSpan(6, 6))
                ),
                25,
                100
        );

        List<TestCell> cells = table.getCells();
        Assertions.assertEquals(3, cells.size());

        cells = table.getCells(new CellSpan(new CellRange(5, 6), new CellRange(1, 6)));
        Assertions.assertEquals(2, cells.size());
        for (Cell c : cells) {
            Assertions.assertTrue(c.getSpan().getRowRange().getStart() >= 5);
        }
    }

    public static class TestCell implements Cell {

        private CellSpan span;

        private final String value;

        public TestCell(String value, CellSpan span) {
            this.span = span;
            this.value = value;
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
            return value;
        }

    }

}
