package de.be.thaw.style.parser.value;

import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

import java.io.File;

/**
 * Parser interface for style values.
 */
public interface StyleValueParser {

    /**
     * Parse the passed source string.
     *
     * @param src              to parse value from
     * @param workingDirectory to resolve relative paths with
     * @return the parsed value
     * @throws StyleValueParseException in case the value could not be parsed
     */
    StyleValue parse(String src, File workingDirectory) throws StyleValueParseException;

}
