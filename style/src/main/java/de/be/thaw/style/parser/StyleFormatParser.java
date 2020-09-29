package de.be.thaw.style.parser;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.Reader;

/**
 * Parser for the Thaw document style format.
 */
public interface StyleFormatParser {

    /**
     * Parse the style model from the passed reader.
     *
     * @param reader           to parse style model from
     * @param workingDirectory used to resolve relative paths
     * @return the parsed style model
     * @throws StyleModelParseException in case the style model could not be parsed
     */
    StyleModel parse(Reader reader, @Nullable File workingDirectory) throws StyleModelParseException;

}
