package de.be.thaw.math.mathml.parser;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.DefaultMathMLParser;
import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.tree.node.impl.SubscriptNode;
import de.be.thaw.math.mathml.tree.node.impl.SubsuperscriptNode;
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

    @Test
    public void simpleSubscriptTest() throws ParseException {
        String src = "<math>\n" +
                "\t<msub subscriptshift=\"0.2\">\n" +
                "\t\t<mi>x</mi>\n" +
                "\t\t<mn>2</mn>\n" +
                "\t</msub>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - msub\n" +
                "    - mi [x]\n" +
                "    - mn [2]\n", tree.toString());

        Assertions.assertEquals(0.2, ((SubscriptNode) tree.getRoot().getChildren().get(0)).getSubscriptShift());
    }

    @Test
    public void simpleSubsupTest() throws ParseException {
        String src = "<math>\n" +
                "\t<msubsup subscriptshift=\"0.2\" superscriptshift=\"0.3\">\n" +
                "\t\t<mi>x</mi>\n" +
                "\t\t<mn>2</mn>\n" +
                "\t\t<mn>3</mn>\n" +
                "\t</msubsup>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - msubsup\n" +
                "    - mi [x]\n" +
                "    - mn [2]\n" +
                "    - mn [3]\n", tree.toString());

        Assertions.assertEquals(0.2, ((SubsuperscriptNode) tree.getRoot().getChildren().get(0)).getSubscriptShift());
        Assertions.assertEquals(0.3, ((SubsuperscriptNode) tree.getRoot().getChildren().get(0)).getSuperscriptShift());
    }

    @Test
    public void simpleRootTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mroot>\n" +
                "\t\t<mi>x</mi>\n" +
                "\t\t<mn>3</mn>\n" +
                "\t</mroot>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - mroot\n" +
                "    - mi [x]\n" +
                "    - mn [3]\n", tree.toString());
    }

    @Test
    public void simpleSqrtTest() throws ParseException {
        String src = "<math>\n" +
                "\t<msqrt>\n" +
                "\t\t<mi>x</mi>\n" +
                "\t</msqrt>\n" +
                "</math>\n";

        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree = parser.parse(new ByteArrayInputStream(src.getBytes()));

        Assertions.assertEquals("- math\n" +
                "  - msqrt\n" +
                "    - mi [x]\n", tree.toString());
    }

}
