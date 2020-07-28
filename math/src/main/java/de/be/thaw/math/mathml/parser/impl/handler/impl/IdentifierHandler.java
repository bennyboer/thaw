package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.IdentifierNode;
import org.w3c.dom.Node;

/**
 * Handler for <mi> MathML identifier nodes.
 */
public class IdentifierHandler extends TokenNodeHandler {

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

        // Parse mathvariant attribute from node
        MathVariant mathVariant = MathVariant.NORMAL;
        if (text.length() == 1) {
            mathVariant = MathVariant.ITALIC; // Default for one character identifiers!
        }
        mathVariant = parseMathVariant(node, mathVariant);

        // Parse mathsize attribute from node
        double mathSize = parseMathSize(node, 1.0);

        return new IdentifierNode(text, mathVariant, mathSize);
    }

}
