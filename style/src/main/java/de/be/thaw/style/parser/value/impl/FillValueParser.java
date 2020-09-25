package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.util.FillStyle;
import de.be.thaw.style.model.style.value.FillStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Style value parser for fill values.
 */
public class FillValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src) throws StyleValueParseException {
        try {
            return new FillStyleValue(FillStyle.valueOf(src.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new StyleValueParseException(String.format(
                    "Could not find fill style with the name '%s'. Available values are: %s",
                    src,
                    Arrays.stream(FillStyle.values()).map(FillStyle::name).map(String::toLowerCase).collect(Collectors.joining(", "))
            ));
        }
    }

}
