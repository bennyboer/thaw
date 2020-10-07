package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.model.style.value.VerticalAlignmentStyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;
import de.be.thaw.util.VerticalAlignment;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Style value parser for vertical alignment values.
 */
public class VerticalAlignmentValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src, File workingDirectory) throws StyleValueParseException {
        try {
            return new VerticalAlignmentStyleValue(VerticalAlignment.valueOf(src.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new StyleValueParseException(String.format(
                    "Could not find vertical alignment with the name '%s'. Available values are: %s",
                    src,
                    Arrays.stream(VerticalAlignment.values()).map(VerticalAlignment::name).map(String::toLowerCase).collect(Collectors.joining(", "))
            ));
        }
    }

}
