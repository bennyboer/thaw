package de.be.thaw.math.mathml.parser.impl.handler;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.util.HorizontalAlignment;
import org.jsoup.nodes.Element;

/**
 * Abstract parse handler.
 */
public abstract class AbstractMathMLNodeParseHandler implements MathMLNodeParseHandler {

    /**
     * Name of the node this handler is able to parse.
     */
    private final String nodeName;

    public AbstractMathMLNodeParseHandler(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Get a string attribute from the passed element.
     *
     * @param element      to get attribute value from
     * @param key          to get attribute value with
     * @param defaultValue the default value in case the element does not have the attribute
     * @return the string attribute value
     */
    public String getStringAttribute(Element element, String key, String defaultValue) {
        if (element.hasAttr(key)) {
            return element.attr(key);
        }

        return defaultValue;
    }

    /**
     * Get a boolean attribute from the passed element.
     *
     * @param element      to get attribute value from
     * @param key          to get attribute value with
     * @param defaultValue the default value in case the element does not have the attribute
     * @return the boolean attribute value
     */
    public boolean getBooleanAttribute(Element element, String key, boolean defaultValue) {
        if (element.hasAttr(key)) {
            return Boolean.parseBoolean(element.attr(key));
        }

        return defaultValue;
    }

    /**
     * Get a double attribute from the passed element.
     *
     * @param element      to get attribute value from
     * @param key          to get attribute value with
     * @param defaultValue the default value in case the element does not have the attribute
     * @return the double attribute value
     * @throws ParseException in case the double could not be parsed from the attribute value
     */
    public double getDoubleAttribute(Element element, String key, double defaultValue) throws ParseException {
        if (element.hasAttr(key)) {
            try {
                return Double.parseDouble(element.attr(key));
            } catch (NumberFormatException e) {
                throw new ParseException(String.format(
                        "Please only provide numbers as value to the attribute '%s' of a '%s' element",
                        key,
                        element.nodeName()
                ));
            }
        }

        return defaultValue;
    }

    /**
     * Get a alignment attribute from the passed element.
     *
     * @param element      to get attribute value from
     * @param key          to get attribute value with
     * @param defaultValue the default value in case the element does not have the attribute
     * @return the alignment attribute value
     * @throws ParseException in case the alignment could not be parsed from the attribute value
     */
    public HorizontalAlignment getAlignmentAttribute(Element element, String key, HorizontalAlignment defaultValue) throws ParseException {
        if (element.hasAttr(key)) {
            try {
                return HorizontalAlignment.valueOf(element.attr(key).toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new ParseException(String.format(
                        "The value '%s' of the attribute '%s' does not match any of the three allowed value 'center', 'left' or 'right'",
                        element.attr(key),
                        key
                ));
            }
        }

        return defaultValue;
    }

}
