package de.be.thaw.info.impl;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.model.author.Author;
import de.be.thaw.info.model.language.Language;

import java.nio.charset.Charset;

/**
 * Implementation of the Thaw document info format.
 */
public class DefaultThawInfo implements ThawInfo {

    /**
     * Encoding of the text files in the project.
     */
    private final Charset encoding;

    /**
     * The language the document is written in.
     */
    private final Language language;

    /**
     * The author of the document.
     */
    private final Author author;

    public DefaultThawInfo(Charset encoding, Language language, Author author) {
        this.encoding = encoding;
        this.language = language;
        this.author = author;
    }

    @Override
    public Charset getEncoding() {
        return encoding;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public Author getAuthor() {
        return author;
    }

}
