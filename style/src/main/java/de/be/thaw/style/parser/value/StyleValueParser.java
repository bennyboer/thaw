package de.be.thaw.style.parser.value;

import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

/**
 * Parser interface for style values.
 */
public interface StyleValueParser {

    /**
     * Parse the passed source string.
     *
     * @param src to parse value from
     * @return the parsed value
     * @throws StyleValueParseException in case the value could not be parsed
     */
    StyleValue parse(String src) throws StyleValueParseException;

}
