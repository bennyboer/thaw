package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.TextNode;
import org.w3c.dom.Node;

/**
 * Handler for <mtext> MathML identifier nodes.
 */
public class TextHandler extends TokenNodeHandler {

    public TextHandler() {
        super("mtext");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        String text = node.getTextContent();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mtext> node without content");
        }

        MathVariant mathVariant = MathVariant.NORMAL;
        if (text.length() == 1) {
            mathVariant = MathVariant.ITALIC; // Default for one character identifiers!
        }

        // Parse mathvariant attribute from node
        mathVariant = parseMathVariant(node, mathVariant);

        // Parse mathsize attribute from node
        double mathSize = parseMathSize(node, 1.0);

        return new TextNode(text, mathVariant, mathSize);
    }

}
