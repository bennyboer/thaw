package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.value.HorizontalAlignmentStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;
import de.be.thaw.util.HorizontalAlignment;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Style value parser for horizontal alignment values.
 */
public class HorizontalAlignmentValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src, File workingDirectory) throws StyleValueParseException {
        try {
            return new HorizontalAlignmentStyleValue(HorizontalAlignment.valueOf(src.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new StyleValueParseException(String.format(
                    "Could not find horizontal alignment with the name '%s'. Available values are: %s",
                    src,
                    Arrays.stream(HorizontalAlignment.values()).map(HorizontalAlignment::name).map(String::toLowerCase).collect(Collectors.joining(", "))
            ));
        }
    }

}
