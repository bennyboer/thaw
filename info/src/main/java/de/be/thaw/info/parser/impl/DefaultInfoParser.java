package de.be.thaw.info.parser.impl;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.impl.DefaultThawInfo;
import de.be.thaw.info.model.author.Author;
import de.be.thaw.info.model.language.Language;
import de.be.thaw.info.parser.InfoParser;
import de.be.thaw.info.parser.exception.ParseException;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Implementation of the Thaw document info format parser.
 */
public class DefaultInfoParser implements InfoParser {

    @Override
    public ThawInfo parse(Reader reader, @Nullable File workingDirectory) throws ParseException {
        Properties properties = new Properties();
        try {
            properties.load(reader);
        } catch (IOException e) {
            throw new ParseException("Could not load the info file to properties", e);
        }

        Charset encoding = parseEncoding(properties);
        Language language = parseLanguage(properties);
        Author author = parseAuthor(properties);

        // Collect variables from properties
        Map<String, String> variables = new HashMap<>();
        for (var entry : properties.entrySet()) {
            variables.put((String) entry.getKey(), (String) entry.getValue());
        }

        DefaultThawInfo info = new DefaultThawInfo(
                encoding,
                language,
                author,
                variables
        );

        info.setBibliographyFile(parseBibliographyFile(properties, workingDirectory));
        String bibliographyStyle = parseBibliographyStyle(properties);
        if (bibliographyStyle != null) {
            info.setBibliographyStyle(bibliographyStyle);
        }

        return info;
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

    /**
     * Parse the bibliography file (if any).
     *
     * @param properties       to use
     * @param workingDirectory we are currently in
     * @return bibliography file (or null if not specified in the info file)
     */
    @Nullable
    private File parseBibliographyFile(Properties properties, @Nullable File workingDirectory) {
        String bibliography = properties.getProperty("bibliography.file");
        if (bibliography == null) {
            return null;
        }

        if (workingDirectory != null) {
            return new File(workingDirectory, bibliography);
        } else {
            return new File(bibliography);
        }
    }

    /**
     * Parse the bibliography style (if any).
     *
     * @param properties to use
     * @return bibliography style (or null if not specified in the info file)
     */
    @Nullable
    private String parseBibliographyStyle(Properties properties) {
        return properties.getProperty("bibliography.style");
    }

}
