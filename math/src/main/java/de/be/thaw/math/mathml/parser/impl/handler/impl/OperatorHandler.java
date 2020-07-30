package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.OperatorNode;
import org.jsoup.nodes.Element;

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
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        String text = element.text();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mo> node without content");
        }

        // Parse attributes
        MathVariant mathVariant = parseMathVariant(element, MathVariant.NORMAL);
        double mathSize = parseMathSize(element, 1.0);
        double lspace = getDoubleAttribute(element, "lspace", DEFAULT_SPACE_WIDTH);
        double rspace = getDoubleAttribute(element, "rspace", DEFAULT_SPACE_WIDTH);

        // TODO Add attributes that we want to support (form, ...)

        // TODO Determine the form attribute automatically based on the operator (opening parathesis, closing, semicolon, ...)

        return new OperatorNode(text, mathVariant, mathSize, lspace, rspace);
    }

}
