package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.NumericNode;
import org.jsoup.nodes.Element;

/**
 * Handler for <mn> MathML numerical nodes.
 */
public class NumericHandler extends TokenNodeHandler {

    public NumericHandler() {
        super("mn");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // This node does only accept any numeric value (3.14, 12, twelve, 3e10, ...)
        String text = element.text();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mn> node without content");
        }

        // Parse mathvariant attribute from node
        MathVariant mathVariant = MathVariant.NORMAL;
        mathVariant = parseMathVariant(element, mathVariant);

        // Parse mathsize attribute from node
        double mathSize = parseMathSize(element, 1.0);

        return new NumericNode(text, mathVariant, mathSize);
    }

}
