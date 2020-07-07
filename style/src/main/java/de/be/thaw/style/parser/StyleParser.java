package de.be.thaw.style.parser;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.parser.exception.ParseException;

import java.io.Reader;

/**
 * Parser for the Thaw document style format.
 */
public interface StyleParser {

    /**
     * Parse the style model from the passed reader.
     *
     * @param reader to parse style model from
     * @return the parsed style model
     * @throws ParseException in case the style model could not be parsed
     */
    StyleModel parse(Reader reader) throws ParseException;

}
