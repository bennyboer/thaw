package de.be.thaw.reference.citation.csl.xml.style.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import de.be.thaw.reference.citation.csl.xml.style.CSLStyle;
import de.be.thaw.reference.citation.csl.xml.style.parser.exception.CSLStyleParseException;

import java.io.IOException;
import java.io.Reader;

/**
 * Parser for CSL styles from XML.
 */
public class XMLCSLStyleParser implements CSLStyleParser {

    @Override
    public CSLStyle parse(Reader reader) throws CSLStyleParseException {
        XmlMapper mapper = new XmlMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            return mapper.readValue(reader, CSLStyle.class);
        } catch (IOException e) {
            throw new CSLStyleParseException(e);
        }
    }

}
