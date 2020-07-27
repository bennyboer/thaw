package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.OperatorNode;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Handler for <mo> MathML operator nodes.
 */
public class OperatorHandler extends AbstractMathMLNodeParseHandler {

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

        // Prepare default values for attributes
        MathVariant mathVariant = MathVariant.NORMAL;
        double lspace = DEFAULT_SPACE_WIDTH;
        double rspace = DEFAULT_SPACE_WIDTH;

        // Parse math variant from node
        Node mathVariantNode = node.getAttributes().getNamedItem("mathvariant");
        if (mathVariantNode != null) {
            mathVariant = MathVariant.forName(mathVariantNode.getTextContent()).orElseThrow(() -> new ParseException(String.format(
                    "Math variant name '%s' is unknown. Try one of the following: [%s]",
                    mathVariantNode.getTextContent(),
                    Arrays.stream(MathVariant.values()).sorted().map(MathVariant::getName).collect(Collectors.joining(", "))
            )));
        }

        // Parse lspace and rspace from node
        Node lspaceNode = node.getAttributes().getNamedItem("lspace");
        if (lspaceNode != null) {
            try {
                lspace = Double.parseDouble(lspaceNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please specify the attribute value of 'lspace' only using a number", e);
            }
        }
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

        return new OperatorNode(text, mathVariant, lspace, rspace);
    }

}
