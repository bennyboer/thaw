package de.be.thaw.math.mathml.parser;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.DefaultMathMLParser;
import de.be.thaw.math.mathml.tree.MathMLTree;
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

        // TODO Create toString method of tree to create unit tests easily
    }

}
