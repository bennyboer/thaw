package de.be.thaw.reference.citation.source.model.parser.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import de.be.thaw.reference.citation.source.model.SourceModel;
import de.be.thaw.reference.citation.source.model.parser.SourceParser;
import de.be.thaw.reference.citation.source.model.parser.exception.ParseException;

import java.io.IOException;
import java.io.Reader;

/**
 * The default source model parser.
 */
public class DefaultSourceParser implements SourceParser {

    @Override
    public SourceModel parse(Reader reader) throws ParseException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader objectReader = mapper.reader();

        try {
            return objectReader.readValue(reader, SourceModel.class);
        } catch (IOException e) {
            throw new ParseException(String.format("Could not parse the provided source format: %s", e.getMessage()), e);
        }
    }

}
