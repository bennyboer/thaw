package de.be.thaw.reference.citation.source.model.parser;

import de.be.thaw.reference.citation.source.model.SourceModel;
import de.be.thaw.reference.citation.source.model.parser.exception.ParseException;

import java.io.Reader;

/**
 * Parser parsing source models.
 */
public interface SourceParser {

    /**
     * Parse the source model from the passed reader.
     *
     * @param reader to parse source model from
     * @return the parsed source model
     * @throws ParseException in case the source model could not be parsed properly
     */
    SourceModel parse(Reader reader) throws ParseException;

}
