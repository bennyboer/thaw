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

    @Test
    public void simpleRowTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mfrac>\n" +
                "\t\t<mi>a</mi>\n" +
                "\t\t<mrow>\n" +
                "\t\t\t<mi>b</mi>\n" +
                "\t\t\t<mo>*</mo>\n" +
                "\t\t\t<mn>2</mn>\n" +
                "\t\t</mrow>\n" +
                "\t</mfrac>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - mfrac\n" +
                "    - mi [a]\n" +
                "    - mrow\n" +
                "      - mi [b]\n" +
                "      - mo [*]\n" +
                "      - mn [2]\n", tree.toString());
    }

    @Test
    public void simpleTextTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mfrac>\n" +
                "\t\t<mi>a</mi>\n" +
                "\t\t<mrow>\n" +
                "\t\t\t<mi>b</mi>\n" +
                "\t\t\t<mo>*</mo>\n" +
                "\t\t\t<mn>2</mn>\n" +
                "\t\t\t<mtext>Hallo Welt</mtext>\n" +
                "\t\t</mrow>\n" +
                "\t</mfrac>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - mfrac\n" +
                "    - mi [a]\n" +
                "    - mrow\n" +
                "      - mi [b]\n" +
                "      - mo [*]\n" +
                "      - mn [2]\n" +
                "      - mtext [Hallo Welt]\n", tree.toString());
    }

    @Test
    public void simpleSuperscriptTest() throws ParseException {
        String src = "<math>\n" +
                "\t<msup>\n" +
                "\t\t<mi>x</mi>\n" +
                "\t\t<mn>2</mn>\n" +
                "\t</msup>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - msup\n" +
                "    - mi [x]\n" +
                "    - mn [2]\n", tree.toString());
    }

}
