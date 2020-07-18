package de.be.thaw.reference.citation.source.impl.book;

import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.Contributor;

import java.util.List;

/**
 * Book accessed online (e. g. PDF, ...).
 */
public class OnlineBook extends Book {

    /**
     * URL where the book can be accessed.
     */
    private String url;

    /**
     * DOI (if available).
     */
    private String doi;

    public OnlineBook(List<Contributor> contributors, String title, Integer year) {
        super(contributors, title, year);
    }

    @Override
    public SourceType getType() {
        return SourceType.ONLINE_BOOK;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

}
