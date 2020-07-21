package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.IdentifierNode;
import org.w3c.dom.Node;

/**
 * Handler for <mi> MathML identifier nodes.
 */
public class IdentifierHandler extends AbstractMathMLNodeParseHandler {

    public IdentifierHandler() {
        super("mi");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        // This node does only accept text content (function names, variable, ...).
        String text = node.getTextContent();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mi> node without content");
        }

        MathMLNode miNode = new IdentifierNode(text);

        // TODO Add attributes that we want to support (mathvariant, ...)

        return miNode;
    }

}
