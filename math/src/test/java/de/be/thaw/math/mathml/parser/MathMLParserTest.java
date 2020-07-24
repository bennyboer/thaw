package de.be.thaw.math.mathml.parser;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.DefaultMathMLParser;
import de.be.thaw.math.mathml.tree.MathMLTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

public class MathMLParserTest {

    @Test
    public void simpleTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mi>a</mi>\n" +
                "\t<mo>+</mo>\n" +
                "\t<mi>b</mi>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - mi [a]\n" +
                "  - mo [+]\n" +
                "  - mi [b]\n", tree.toString());
    }

    @Test
    public void simpleNumericTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mn>1</mn>\n" +
                "\t<mo>+</mo>\n" +
                "\t<mn>1</mn>\t\n" +
                "\t<mo>=</mo>\n" +
                "\t<mn>2</mn>\n" +
                "</math>";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - mn [1]\n" +
                "  - mo [+]\n" +
                "  - mn [1]\n" +
                "  - mo [=]\n" +
                "  - mn [2]\n", tree.toString());
    }

    @Test
    public void simpleFractionTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mfrac>\n" +
                "\t\t<mn>1</mn>\n" +
                "\t\t<mn>2</mn>\n" +
                "\t</mfrac>\n" +
                "</math>";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - mfrac\n" +
                "    - mn [1]\n" +
                "    - mn [2]\n", tree.toString());
    }

}
