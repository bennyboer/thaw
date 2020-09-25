package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.value.StringStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

/**
 * Style value parser for simple string values.
 */
public class StringValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src) throws StyleValueParseException {
        return new StringStyleValue(src);
    }

}
