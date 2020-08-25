package de.be.thaw.math.mathml.parser;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.DefaultMathMLParser;
import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.FractionNode;
import de.be.thaw.math.mathml.tree.node.impl.IdentifierNode;
import de.be.thaw.math.mathml.tree.node.impl.OverNode;
import de.be.thaw.math.mathml.tree.node.impl.PaddedNode;
import de.be.thaw.math.mathml.tree.node.impl.SpaceNode;
import de.be.thaw.math.mathml.tree.node.impl.SubscriptNode;
import de.be.thaw.math.mathml.tree.node.impl.SubsuperscriptNode;
import de.be.thaw.math.mathml.tree.node.impl.UnderNode;
import de.be.thaw.math.mathml.tree.node.impl.UnderOverNode;
import de.be.thaw.util.HorizontalAlignment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class MathMLParserTest {

    private MathMLParser parser;

    @BeforeEach
    public void beforeEach() {
        parser = new DefaultMathMLParser();
    }

    private MathMLTree parse(String str) throws ParseException {
        return parser.parse(
                new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_16)),
                StandardCharsets.UTF_16,
                new MathMLParserConfig(1.0)
        );
    }

    @Test
    public void simpleTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mi>a</mi>\n" +
                "\t<mo>+</mo>\n" +
                "\t<mi>b</mi>\n" +
                "</math>\n";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - mi [a]\n" +
                "  - mo [+]\n" +
                "  - mi [b]\n", tree.toString());
    }

    @Test
    public void simpleIdentifierAttributesTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mi mathsize=\"0.75\" mathvariant=\"double-struck\">a</mi>\n" +
                "\t<mo>+</mo>\n" +
                "\t<mi>b</mi>\n" +
                "</math>\n";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - mi [a]\n" +
                "  - mo [+]\n" +
                "  - mi [b]\n", tree.toString());

        IdentifierNode identifierNode = (IdentifierNode) tree.getRoot().getChildren().get(0);
        Assertions.assertEquals(0.75, identifierNode.getSizeFactor());
        Assertions.assertEquals(MathVariant.DOUBLE_STRUCK, identifierNode.getMathVariant());
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

        MathMLTree tree = parse(src);

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

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - mfrac\n" +
                "    - mn [1]\n" +
                "    - mn [2]\n", tree.toString());
    }

    @Test
    public void fractionAttributesTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mfrac numalign=\"left\" denomalign=\"right\" linethickness=\"2.0\" bevelled=\"true\">\n" +
                "\t\t<mn>1</mn>\n" +
                "\t\t<mn>2</mn>\n" +
                "\t</mfrac>\n" +
                "</math>";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - mfrac\n" +
                "    - mn [1]\n" +
                "    - mn [2]\n", tree.toString());

        FractionNode fractionNode = (FractionNode) tree.getRoot().getChildren().get(0);
        Assertions.assertEquals(HorizontalAlignment.LEFT, fractionNode.getNumeratorAlignment());
        Assertions.assertEquals(HorizontalAlignment.RIGHT, fractionNode.getDenominatorAlignment());
        Assertions.assertEquals(2.0, fractionNode.getLineThickness());
        Assertions.assertEquals(true, fractionNode.isBevelled());
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

        MathMLTree tree = parse(src);

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

        MathMLTree tree = parse(src);

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

        MathMLTree tree = parse(src);

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

        MathMLTree tree = parse(src);

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

        MathMLTree tree = parse(src);

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

        MathMLTree tree = parse(src);

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

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - msqrt\n" +
                "    - mi [x]\n", tree.toString());
    }

    @Test
    public void simpleOverTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mover>\n" +
                "\t\t<mrow>\n" +
                "\t\t\t<mi>x</mi>\n" +
                "\t\t\t<mo>+</mo>\n" +
                "\t\t\t<mi>y</mi>\n" +
                "\t\t\t<mo>+</mo>\n" +
                "\t\t\t<mi>z</mi>\n" +
                "\t\t</mrow>\n" +
                "\t\t<mo>!</mo>\n" +
                "\t</mover>\n" +
                "</math>\n";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - mover\n" +
                "    - mrow\n" +
                "      - mi [x]\n" +
                "      - mo [+]\n" +
                "      - mi [y]\n" +
                "      - mo [+]\n" +
                "      - mi [z]\n" +
                "    - mo [!]\n", tree.toString());

        OverNode overNode = (OverNode) tree.getRoot().getChildren().get(0);
        Assertions.assertEquals(HorizontalAlignment.CENTER, overNode.getAlignment());
    }

    @Test
    public void simpleUnderTest() throws ParseException {
        String src = "<math>\n" +
                "\t<munder align=\"left\">\n" +
                "\t\t<mrow>\n" +
                "\t\t\t<mi>x</mi>\n" +
                "\t\t\t<mo>+</mo>\n" +
                "\t\t\t<mi>y</mi>\n" +
                "\t\t\t<mo>+</mo>\n" +
                "\t\t\t<mi>z</mi>\n" +
                "\t\t</mrow>\n" +
                "\t\t<mo>!</mo>\n" +
                "\t</munder>\n" +
                "</math>\n";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - munder\n" +
                "    - mrow\n" +
                "      - mi [x]\n" +
                "      - mo [+]\n" +
                "      - mi [y]\n" +
                "      - mo [+]\n" +
                "      - mi [z]\n" +
                "    - mo [!]\n", tree.toString());

        UnderNode underNode = (UnderNode) tree.getRoot().getChildren().get(0);
        Assertions.assertEquals(HorizontalAlignment.LEFT, underNode.getAlignment());
    }

    @Test
    public void simpleUnderOverTest() throws ParseException {
        String src = "<math>\n" +
                "\t<munderover align=\"right\">\n" +
                "\t\t<mrow>\n" +
                "\t\t\t<mi>x</mi>\n" +
                "\t\t\t<mo>+</mo>\n" +
                "\t\t\t<mi>y</mi>\n" +
                "\t\t\t<mo>+</mo>\n" +
                "\t\t\t<mi>z</mi>\n" +
                "\t\t</mrow>\n" +
                "\t\t<mo>!</mo>\n" +
                "\t\t<mi>x</mi>\n" +
                "\t</munderover>\n" +
                "</math>\n";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - munderover\n" +
                "    - mrow\n" +
                "      - mi [x]\n" +
                "      - mo [+]\n" +
                "      - mi [y]\n" +
                "      - mo [+]\n" +
                "      - mi [z]\n" +
                "    - mo [!]\n" +
                "    - mi [x]\n", tree.toString());

        UnderOverNode underOverNode = (UnderOverNode) tree.getRoot().getChildren().get(0);
        Assertions.assertEquals(HorizontalAlignment.RIGHT, underOverNode.getAlignment());
    }

    @Test
    public void simpleSpaceTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mi>x</mi>\n" +
                "\t<mspace depth=\"2\" height=\"4\" width=\"5\" />\n" +
                "\t<mi>y</mi>\n" +
                "</math>\n";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - mi [x]\n" +
                "  - mspace\n" +
                "  - mi [y]\n", tree.toString());

        SpaceNode spaceNode = (SpaceNode) tree.getRoot().getChildren().get(1);
        Assertions.assertEquals(2, spaceNode.getDepth());
        Assertions.assertEquals(4, spaceNode.getHeight());
        Assertions.assertEquals(5, spaceNode.getWidth());
    }

    @Test
    public void simplePaddedTest() throws ParseException {
        String src = "<math>\n" +
                "\t<mi>x</mi>\n" +
                "\t<mpadded depth=\"+2\" height=\"-4\" width=\"2width\" lspace=\"+1\" voffset=\"+5\">\n" +
                "\t\t<mi>pi</mi>\n" +
                "\t</mpadded>\n" +
                "\t<mi>y</mi>\n" +
                "</math>\n";

        MathMLTree tree = parse(src);

        Assertions.assertEquals("- math\n" +
                "  - mi [x]\n" +
                "  - mpadded\n" +
                "    - mi [pi]\n" +
                "  - mi [y]\n", tree.toString());

        PaddedNode paddedNode = (PaddedNode) tree.getRoot().getChildren().get(1);
        Assertions.assertEquals("2width", paddedNode.getWidthAdjustment());
        Assertions.assertEquals("-4", paddedNode.getHeightAdjustment());
        Assertions.assertEquals("+2", paddedNode.getDepthAdjustment());
        Assertions.assertEquals("+1", paddedNode.getXAdjustment());
        Assertions.assertEquals("+5", paddedNode.getYAdjustment());
    }

}
