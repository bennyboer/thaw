package de.be.thaw.reference.citation.source.impl;

import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.contributor.Author;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A website as source.
 */
public class Website extends AbstractSource {

    /**
     * Optional authors of the source.
     */
    private List<Author> authors = Collections.emptyList();

    /**
     * Date of the publication.
     */
    private Date publicationDate;

    /**
     * Title of the page or article.
     */
    private final String title;

    /**
     * URL of the website source.
     */
    private final String url;

    /**
     * Date of the website retrieval.
     * Required since websites are likely to change over time.
     */
    private final Date retrievalDate;

    public Website(String identifier, String title, String url, Date retrievalDate) {
        super(identifier);

        this.title = title;
        this.url = url;
        this.retrievalDate = retrievalDate;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Date getRetrievalDate() {
        return retrievalDate;
    }

    @Override
    public SourceType getType() {
        return SourceType.WEBSITE;
    }

}
