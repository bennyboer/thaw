package de.be.thaw.reference.citation.source.impl;

import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.contributor.Author;

import java.util.List;

/**
 * An article source.
 */
public class Article implements Source {

    /**
     * Authors of the article.
     */
    private final List<Author> authors;

    /**
     * Year the article was published.
     */
    private final Integer year;

    /**
     * Title of the article.
     */
    private final String title;

    /**
     * Name of the journal.
     */
    private final String journalName;

    /**
     * Volumne of the journal.
     */
    private final int volume;

    /**
     * Number of the journal.
     */
    private final int number;

    /**
     * The page range of the article in the journal.
     */
    private final String pages;

    /**
     * The DOI of the article.
     */
    private final String doi;

    public Article(List<Author> authors, Integer year, String title, String journalName, int volume, int number, String pages, String doi) {
        this.authors = authors;
        this.year = year;
        this.title = title;
        this.journalName = journalName;
        this.volume = volume;
        this.number = number;
        this.pages = pages;
        this.doi = doi;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public Integer getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public String getJournalName() {
        return journalName;
    }

    public int getVolume() {
        return volume;
    }

    public int getNumber() {
        return number;
    }

    public String getPages() {
        return pages;
    }

    public String getDoi() {
        return doi;
    }

    @Override
    public SourceType getType() {
        return SourceType.ARTICLE;
    }

}
