package de.be.thaw.info;

import de.be.thaw.info.model.author.Author;
import de.be.thaw.info.model.language.Language;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * Info of a Thaw project.
 */
public interface ThawInfo {

    /**
     * Get the defined encoding.
     *
     * @return encoding
     */
    Charset getEncoding();

    /**
     * Language the document is written in.
     *
     * @return language
     */
    Language getLanguage();

    /**
     * Get the author of the document.
     *
     * @return author
     */
    Author getAuthor();

    /**
     * Get the bibliography file to use.
     *
     * @return bibliography file
     */
    Optional<File> getBibliographyFile();

    /**
     * Get the bibliography style name (citation style).
     *
     * @return bibliography style name
     */
    String getBibliographyStyle();

    /**
     * Get a variable defined in the info file.
     *
     * @param key to get variable for
     * @return variable (or empty Optional)
     */
    Optional<String> getVariable(String key);

}
