package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.NumericNode;
import org.w3c.dom.Node;

/**
 * Handler for <mn> MathML numerical nodes.
 */
public class NumericHandler extends AbstractMathMLNodeParseHandler {

    public NumericHandler() {
        super("mn");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        // This node does only accept any numeric value (3.14, 12, twelve, 3e10, ...)
        String text = node.getTextContent();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mn> node without content");
        }

        MathMLNode mnNode = new NumericNode(text);

        // TODO Add attributes that we want to support (mathvariant, ...)

        return mnNode;
    }

}
