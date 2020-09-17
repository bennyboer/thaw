package de.be.thaw.style.parser.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.parser.StyleParser;
import de.be.thaw.style.parser.exception.StyleModelParseException;

import java.io.IOException;
import java.io.Reader;

/**
 * The default style parser.
 */
public class DefaultStyleParser implements StyleParser {

    @Override
    public StyleModel parse(Reader reader) throws StyleModelParseException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader objectReader = mapper.reader();

        try {
            return objectReader.readValue(reader, StyleModel.class);
        } catch (IOException e) {
            throw new StyleModelParseException(String.format("Could not parse the provided style format: %s", e.getMessage()), e);
        }
    }

}
