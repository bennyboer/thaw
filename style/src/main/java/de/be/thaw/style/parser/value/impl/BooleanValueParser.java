package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.value.BooleanStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

/**
 * Style value parser for boolean values.
 */
public class BooleanValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src) throws StyleValueParseException {
        return new BooleanStyleValue(Boolean.parseBoolean(src));
    }

}
