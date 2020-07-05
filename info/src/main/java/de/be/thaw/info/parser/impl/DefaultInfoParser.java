package de.be.thaw.info.parser.impl;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.impl.DefaultThawInfo;
import de.be.thaw.info.model.author.Author;
import de.be.thaw.info.model.language.Language;
import de.be.thaw.info.parser.InfoParser;
import de.be.thaw.info.parser.exception.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Objects;
import java.util.Properties;

/**
 * Implementation of the Thaw document info format parser.
 */
public class DefaultInfoParser implements InfoParser {

    @Override
    public ThawInfo parse(Reader reader) throws ParseException {
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            throw new ParseException("Could not load the info file to properties", e);
        }

        Charset encoding = parseEncoding(properties);
        Language language = parseLanguage(properties);
        Author author = parseAuthor(properties);

        return new DefaultThawInfo(
                encoding,
                language,
                author
        );
    }

    /**
     * Get the encoding from the passed properties.
     *
     * @param properties to use
     * @return encoding
     * @throws ParseException in case the encoding is unknown
     */
    private Charset parseEncoding(Properties properties) throws ParseException {
        String encoding = properties.getProperty("encoding");
        if (encoding == null) {
            return Charset.defaultCharset();
        }

        try {
            return Charset.forName(encoding);
        } catch (IllegalCharsetNameException e) {
            throw new ParseException(String.format("The encoding name '%s' is unknown", encoding), e);
        }
    }

    /**
     * Parse the language from the given properties.
     *
     * @param properties to use
     * @return language
     */
    private Language parseLanguage(Properties properties) {
        String code = properties.getProperty("language");
        if (code == null) {
            return Language.ENGLISH;
        }

        return Language.forCode(code);
    }

    /**
     * Parse the author.
     *
     * @param properties to use
     * @return author
     */
    private Author parseAuthor(Properties properties) {
        String name = Objects.requireNonNullElse(properties.getProperty("author.name"), "Unknown");
        String email = Objects.requireNonNullElse(properties.getProperty("author.email"), "");

        return new Author(name, email);
    }

}
