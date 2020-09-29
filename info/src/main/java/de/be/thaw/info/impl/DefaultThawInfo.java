package de.be.thaw.info.impl;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.model.author.Author;
import de.be.thaw.info.model.language.Language;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the Thaw document info format.
 */
public class DefaultThawInfo implements ThawInfo {

    /**
     * The default citation style name.
     */
    private static final String DEFAULT_CITATION_STYLE_NAME = "apa";

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

    /**
     * The bibliography file to use.
     */
    @Nullable
    private File bibliographyFile;

    /**
     * The bibliography or citation style name.
     */
    private String bibliographyStyle = DEFAULT_CITATION_STYLE_NAME;

    /**
     * Map holding available variables.
     */
    private final Map<String, String> variables;

    public DefaultThawInfo(Charset encoding, Language language, Author author, Map<String, String> variables) {
        this.encoding = encoding;
        this.language = language;
        this.author = author;
        this.variables = variables;
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

    @Override
    public Optional<File> getBibliographyFile() {
        return Optional.ofNullable(bibliographyFile);
    }

    /**
     * Set the bibliography file to use.
     *
     * @param bibliographyFile to set
     */
    public void setBibliographyFile(@Nullable File bibliographyFile) {
        this.bibliographyFile = bibliographyFile;
    }

    @Override
    public String getBibliographyStyle() {
        return bibliographyStyle;
    }

    @Override
    public Optional<String> getVariable(String key) {
        if (variables == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(variables.get(key));
    }

    /**
     * Set the name of the bibliography or citation style to use.
     *
     * @param bibliographyStyle name of the style to use
     */
    public void setBibliographyStyle(String bibliographyStyle) {
        this.bibliographyStyle = bibliographyStyle;
    }

}
