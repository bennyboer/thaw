package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.util.list.ListStyleType;
import de.be.thaw.style.model.style.value.ListStyleTypeStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Style value parser for list style types.
 */
public class ListStyleTypeValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src) throws StyleValueParseException {
        try {
            return new ListStyleTypeStyleValue(ListStyleType.valueOf(src.toUpperCase().replace("-", "_")));
        } catch (IllegalArgumentException e) {
            throw new StyleValueParseException(String.format(
                    "Could not find list style type with the name '%s'. Available values are: %s",
                    src,
                    Arrays.stream(ListStyleType.values()).map(ListStyleType::name).map(String::toLowerCase).collect(Collectors.joining(", "))
            ));
        }
    }

}
