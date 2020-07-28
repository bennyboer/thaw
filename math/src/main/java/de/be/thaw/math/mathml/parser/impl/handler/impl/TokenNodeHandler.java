package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Abstract handler for token nodes.
 */
public abstract class TokenNodeHandler extends AbstractMathMLNodeParseHandler {

    public TokenNodeHandler(String nodeName) {
        super(nodeName);
    }

    /**
     * Parse the math variant from the passed node.
     *
     * @param node         to parse attribute from
     * @param defaultValue the default value in case the attribute has not been specified
     * @return the parsed math variant
     * @throws ParseException in case the math variant could not be determined from the node
     */
    protected MathVariant parseMathVariant(Node node, MathVariant defaultValue) throws ParseException {
        Node mathVariantNode = node.getAttributes().getNamedItem("mathvariant");
        if (mathVariantNode != null) {
            return MathVariant.forName(mathVariantNode.getTextContent()).orElseThrow(() -> new ParseException(String.format(
                    "Math variant name '%s' is unknown. Try one of the following: [%s]",
                    mathVariantNode.getTextContent(),
                    Arrays.stream(MathVariant.values()).sorted().map(MathVariant::getName).collect(Collectors.joining(", "))
            )));
        }

        return defaultValue;
    }

    /**
     * Parse the math size attribute from the passed node.
     *
     * @param node         to parse attribute from
     * @param defaultValue the default value in case the attribute has not been specified
     * @return the parsed math size
     * @throws ParseException in case the math size could not be determined from the node
     */
    protected double parseMathSize(Node node, double defaultValue) throws ParseException {
        Node mathSizeNode = node.getAttributes().getNamedItem("mathsize");
        if (mathSizeNode != null) {
            try {
                return Double.parseDouble(mathSizeNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please provide only numbers as value of the mathsize attribute. For example 0.6 meaning 60 % of the normal font size.");
            }
        }

        return defaultValue;
    }

}
