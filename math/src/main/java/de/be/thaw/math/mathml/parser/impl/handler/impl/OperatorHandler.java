package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.OperatorNode;
import org.w3c.dom.Node;

/**
 * Handler for <mo> MathML operator nodes.
 */
public class OperatorHandler extends TokenNodeHandler {

    /**
     * Default space width.
     * Default value for lspace and rspace attributes.
     */
    private static final double DEFAULT_SPACE_WIDTH = 3.0;

    public OperatorHandler() {
        super("mo");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        String text = node.getTextContent();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mo> node without content");
        }

        // Parse math variant from node
        MathVariant mathVariant = MathVariant.NORMAL;
        mathVariant = parseMathVariant(node, mathVariant);

        // Parse mathsize attribute from node
        double mathSize = parseMathSize(node, 1.0);

        // Parse lspace and rspace from node
        double lspace = DEFAULT_SPACE_WIDTH;
        Node lspaceNode = node.getAttributes().getNamedItem("lspace");
        if (lspaceNode != null) {
            try {
                lspace = Double.parseDouble(lspaceNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please specify the attribute value of 'lspace' only using a number", e);
            }
        }
        double rspace = DEFAULT_SPACE_WIDTH;
        Node rspaceNode = node.getAttributes().getNamedItem("rspace");
        if (rspaceNode != null) {
            try {
                rspace = Double.parseDouble(rspaceNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please specify the attribute value of 'rspace' only using a number", e);
            }
        }

        // TODO Add attributes that we want to support (form, ...)

        // TODO Determine the form attribute automatically based on the operator (opening parathesis, closing, semicolon, ...)

        return new OperatorNode(text, mathVariant, mathSize, lspace, rspace);
    }

}
