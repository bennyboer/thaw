package de.be.thaw.style.parser;

import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.parser.exception.StyleModelParseException;

import java.io.Reader;

/**
 * Parser for the Thaw document style format.
 */
public interface StyleFormatParser {

    /**
     * Parse the style model from the passed reader.
     *
     * @param reader to parse style model from
     * @return the parsed style model
     * @throws StyleModelParseException in case the style model could not be parsed
     */
    DefaultStyleModel parse(Reader reader) throws StyleModelParseException;

}
