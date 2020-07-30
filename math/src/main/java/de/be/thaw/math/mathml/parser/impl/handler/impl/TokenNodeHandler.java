package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import org.jsoup.nodes.Element;

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
     * @param element      to parse attribute from
     * @param defaultValue the default value in case the attribute has not been specified
     * @return the parsed math variant
     * @throws ParseException in case the math variant could not be determined from the node
     */
    protected MathVariant parseMathVariant(Element element, MathVariant defaultValue) throws ParseException {
        String key = "mathvariant";

        if (element.hasAttr(key)) {
            return MathVariant.forName(element.attr(key)).orElseThrow(() -> new ParseException(String.format(
                    "Math variant name '%s' is unknown. Try one of the following: [%s]",
                    element.attr(key),
                    Arrays.stream(MathVariant.values()).sorted().map(MathVariant::getName).collect(Collectors.joining(", "))
            )));
        }

        return defaultValue;
    }

    /**
     * Parse the math size attribute from the passed node.
     *
     * @param element      to parse attribute from
     * @param defaultValue the default value in case the attribute has not been specified
     * @return the parsed math size
     * @throws ParseException in case the math size could not be determined from the node
     */
    protected double parseMathSize(Element element, double defaultValue) throws ParseException {
        return getDoubleAttribute(element, "mathsize", defaultValue);
    }

}
