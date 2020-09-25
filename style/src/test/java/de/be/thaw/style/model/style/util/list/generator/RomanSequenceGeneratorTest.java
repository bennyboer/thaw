package de.be.thaw.style.model.style.util.list.generator;

import de.be.thaw.style.model.style.util.list.ListStyleType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RomanSequenceGeneratorTest {

    @Test
    public void one() {
        Assertions.assertEquals("I", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(1));
        Assertions.assertEquals("i", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(1));
    }

    @Test
    public void two() {
        Assertions.assertEquals("II", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(2));
        Assertions.assertEquals("ii", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(2));
    }

    @Test
    public void three() {
        Assertions.assertEquals("III", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(3));
        Assertions.assertEquals("iii", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(3));
    }

    @Test
    public void four() {
        Assertions.assertEquals("IV", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(4));
        Assertions.assertEquals("iv", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(4));
    }

    @Test
    public void ten() {
        Assertions.assertEquals("X", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(10));
        Assertions.assertEquals("x", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(10));
    }

    @Test
    public void fifty() {
        Assertions.assertEquals("L", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(50));
        Assertions.assertEquals("l", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(50));
    }

    @Test
    public void hundred() {
        Assertions.assertEquals("C", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(100));
        Assertions.assertEquals("c", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(100));
    }

    @Test
    public void fiveHundred() {
        Assertions.assertEquals("D", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(500));
        Assertions.assertEquals("d", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(500));
    }

    @Test
    public void thousand() {
        Assertions.assertEquals("M", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(1000));
        Assertions.assertEquals("m", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(1000));
    }

    @Test
    public void complex() {
        Assertions.assertEquals("MMXV", ListStyleType.UPPER_ROMAN.getSymbolGenerator().generate(2015));
        Assertions.assertEquals("mmxv", ListStyleType.LOWER_ROMAN.getSymbolGenerator().generate(2015));
    }

}
