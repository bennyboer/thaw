package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.value.ColorStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;
import de.be.thaw.util.color.Color;

/**
 * Style value parser for color values.
 */
public class ColorValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src) throws StyleValueParseException {
        src = src.trim();

        Color result;
        if (src.startsWith("#")) {
            result = fromHex(src);
        } else if (src.startsWith("rgba(")) {
            result = fromRGBAExpression(src);
        } else if (src.startsWith("rgb(")) {
            result = fromRGBExpression(src);
        } else {
            throw new StyleValueParseException(String.format(
                    "The color style value parser cannot understand the value '%s'. Consider writing a color value using either for example '#FF0000', rgb(1.0, 0.0, 0.0) or rgba(1.0, 0.0, 0.0, 1.0).",
                    src
            ));
        }

        return new ColorStyleValue(result);
    }

    /**
     * Parse a color from a hex string.
     *
     * @param src the hex string starting with '#'
     * @return the parsed color
     * @throws StyleValueParseException in case the color could not be parsed
     */
    private Color fromHex(String src) throws StyleValueParseException {
        try {
            return new Color(
                    Integer.valueOf(src.substring(1, 3), 16) / (double) 255,
                    Integer.valueOf(src.substring(3, 5), 16) / (double) 255,
                    Integer.valueOf(src.substring(5, 7), 16) / (double) 255
            );
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Tried to pass a hex color value '%s' that has an incorrect format. For example #FF0000 would be correct.",
                    src
            ));
        }
    }

    /**
     * Parse a color from a rgb(1.0, 0.0, 0.0) expression.
     *
     * @param src the expression
     * @return the parsed color
     * @throws StyleValueParseException in case the color could not be parsed
     */
    private Color fromRGBExpression(String src) throws StyleValueParseException {
        // Removing 'rbg(' at the beginning and ')' at the end.
        src = src.substring(4, src.length() - 1);

        String[] parts = src.split(",");
        if (parts.length != 3) {
            throw new StyleValueParseException(String.format(
                    "Expected rgb() color expression to have 3 arguments, instead got %d. Got expression 'rgb(%s)'",
                    parts.length,
                    src
            ));
        }

        double red;
        try {
            red = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Could not parse argument '%s' as number of the rgb expression 'rgb(%s)'",
                    parts[0],
                    src
            ));
        }

        double green;
        try {
            green = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Could not parse argument '%s' as number of the rgb expression 'rgb(%s)'",
                    parts[1],
                    src
            ));
        }

        double blue;
        try {
            blue = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Could not parse argument '%s' as number of the rgb expression 'rgb(%s)'",
                    parts[2],
                    src
            ));
        }

        return new Color(red, green, blue);
    }

    /*
     * Parse a color from a rgba(1.0, 0.0, 0.0, 1.0) expression.
     * @param src the expression
     * @return the parsed color
     * @throws StyleValueParseException in case the color could not be parsed
     */
    private Color fromRGBAExpression(String src) throws StyleValueParseException {
        // Removing 'rbga(' at the beginning and ')' at the end.
        src = src.substring(5, src.length() - 1);

        String[] parts = src.split(",");
        if (parts.length != 4) {
            throw new StyleValueParseException(String.format(
                    "Expected rgba() color expression to have 3 arguments, instead got %d. Got expression 'rgba(%s)'",
                    parts.length,
                    src
            ));
        }

        double red;
        try {
            red = Double.parseDouble(parts[0]);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Could not parse argument '%s' as number of the rgb expression 'rgba(%s)'",
                    parts[0],
                    src
            ));
        }

        double green;
        try {
            green = Double.parseDouble(parts[1]);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Could not parse argument '%s' as number of the rgb expression 'rgba(%s)'",
                    parts[1],
                    src
            ));
        }

        double blue;
        try {
            blue = Double.parseDouble(parts[2]);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Could not parse argument '%s' as number of the rgb expression 'rgba(%s)'",
                    parts[2],
                    src
            ));
        }

        double alpha;
        try {
            alpha = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(String.format(
                    "Could not parse argument '%s' as number of the rgb expression 'rgba(%s)'",
                    parts[3],
                    src
            ));
        }

        return new Color(red, green, blue, alpha);
    }

}
