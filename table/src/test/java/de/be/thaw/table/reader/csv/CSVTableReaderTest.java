package de.be.thaw.table.reader.csv;

import de.be.thaw.table.Table;
import de.be.thaw.table.impl.DefaultTableTest;
import de.be.thaw.table.reader.TableReader;
import de.be.thaw.table.reader.exception.TableReadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class CSVTableReaderTest {

    @Test
    void simpleTest() throws TableReadException {
        TableReader<DefaultTableTest.TestCell> reader = new CSVTableReader<>("|", 25, 100);
        Table<DefaultTableTest.TestCell> table = reader.read(new StringReader("A | B | C\n" +
                "D | E | F\n" +
                "G | H | I\n" +
                "J | K | L\n" +
                "M | N | O\n" +
                "P | Q | R\n" +
                "S | T | U\n" +
                "V | W | X"), value -> new DefaultTableTest.TestCell(value, null));

        Assertions.assertEquals("| A | B | C |\n" +
                "| D | E | F |\n" +
                "| G | H | I |\n" +
                "| J | K | L |\n" +
                "| M | N | O |\n" +
                "| P | Q | R |\n" +
                "| S | T | U |\n" +
                "| V | W | X |", table.toString());
    }

}
