package de.be.thaw.reference.citation.csl.xml.style.parser;

import de.be.thaw.reference.citation.csl.xml.style.CSLStyle;
import de.be.thaw.reference.citation.csl.xml.style.parser.exception.CSLStyleParseException;

import java.io.Reader;

/**
 * Parser for CSL styles.
 */
public interface CSLStyleParser {

    /**
     * Parse the CSL style source format provided by the given reader.
     *
     * @param reader to read CSL style from
     * @return the parsed CSL style
     * @throws CSLStyleParseException in case the style could not be read
     */
    CSLStyle parse(Reader reader) throws CSLStyleParseException;

}
