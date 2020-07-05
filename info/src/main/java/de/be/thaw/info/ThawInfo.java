package de.be.thaw.info;

import de.be.thaw.info.model.author.Author;
import de.be.thaw.info.model.language.Language;

import java.nio.charset.Charset;

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

}
