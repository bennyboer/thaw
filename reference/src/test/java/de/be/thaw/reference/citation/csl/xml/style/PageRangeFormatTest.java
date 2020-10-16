package de.be.thaw.reference.citation.csl.xml.style;

import de.be.thaw.reference.citation.csl.xml.style.options.PageRangeFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PageRangeFormatTest {

    @Test
    void unformattedTest() {
        Assertions.assertEquals("324-546", PageRangeFormat.UNFORMATTED.getFormatter().format(324, 546, "-"));
        Assertions.assertEquals("2342-6", PageRangeFormat.UNFORMATTED.getFormatter().format(2342, 6, "-"));
    }

    @Test
    void expandedTest() {
        Assertions.assertEquals("2342-2346", PageRangeFormat.EXPANDED.getFormatter().format(2342, 6, "-"));
        Assertions.assertEquals("2342-2356", PageRangeFormat.EXPANDED.getFormatter().format(2342, 56, "-"));
        Assertions.assertEquals("2342-2456", PageRangeFormat.EXPANDED.getFormatter().format(2342, 456, "-"));
        Assertions.assertEquals("2342-2356", PageRangeFormat.EXPANDED.getFormatter().format(2342, 2356, "-"));
    }

    @Test
    void minimalTest() {
        Assertions.assertEquals("2342-6", PageRangeFormat.MINIMAL.getFormatter().format(2342, 2346, "-"));
        Assertions.assertEquals("2342-56", PageRangeFormat.MINIMAL.getFormatter().format(2342, 2356, "-"));
        Assertions.assertEquals("2342-456", PageRangeFormat.MINIMAL.getFormatter().format(2342, 2456, "-"));
        Assertions.assertEquals("2342-6356", PageRangeFormat.MINIMAL.getFormatter().format(2342, 6356, "-"));
        Assertions.assertEquals("2342-56", PageRangeFormat.MINIMAL.getFormatter().format(2342, 356, "-"));
    }

    @Test
    void minimalTwoTest() {
        Assertions.assertEquals("2342-46", PageRangeFormat.MINIMAL_TWO.getFormatter().format(2342, 2346, "-"));
        Assertions.assertEquals("2342-56", PageRangeFormat.MINIMAL_TWO.getFormatter().format(2342, 2356, "-"));
        Assertions.assertEquals("2342-456", PageRangeFormat.MINIMAL_TWO.getFormatter().format(2342, 2456, "-"));
        Assertions.assertEquals("2342-6356", PageRangeFormat.MINIMAL_TWO.getFormatter().format(2342, 6356, "-"));
        Assertions.assertEquals("2342-56", PageRangeFormat.MINIMAL_TWO.getFormatter().format(2342, 56, "-"));
    }

    @Test
    void chicagoTest() {
        Assertions.assertEquals("71-72", PageRangeFormat.CHICAGO.getFormatter().format(71, 72, "-"));
        Assertions.assertEquals("1100-1123", PageRangeFormat.CHICAGO.getFormatter().format(1100, 1123, "-"));
        Assertions.assertEquals("505-17", PageRangeFormat.CHICAGO.getFormatter().format(505, 17, "-"));
        Assertions.assertEquals("1002-6", PageRangeFormat.CHICAGO.getFormatter().format(1002, 6, "-"));
        Assertions.assertEquals("415-532", PageRangeFormat.CHICAGO.getFormatter().format(415, 532, "-"));
        Assertions.assertEquals("11564-68", PageRangeFormat.CHICAGO.getFormatter().format(11564, 68, "-"));
        Assertions.assertEquals("1496-1504", PageRangeFormat.CHICAGO.getFormatter().format(1496, 1504, "-"));
        Assertions.assertEquals("2787-2816", PageRangeFormat.CHICAGO.getFormatter().format(2787, 2816, "-"));
    }

}
