package de.be.thaw.reference.citation.source.impl.book;

import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.contributor.Contributor;
import de.be.thaw.reference.citation.source.impl.AbstractSource;

import java.util.List;

/**
 * A book source.
 */
public class Book extends AbstractSource {

    /**
     * List of contributors to the book.
     */
    private final List<Contributor> contributors;

    /**
     * Title of the book.
     */
    private final String title;

    /**
     * Year of the books appearance.
     */
    private final Integer year;

    /**
     * City of where the book appeared (if any).
     */
    private String city;

    /**
     * Country of where the book appeared (if any).
     */
    private String country;

    /**
     * Name of the publisher of the book (if any).
     */
    private String publisher;

    /**
     * Edition of the book (if any).
     */
    private String edition;

    public Book(String identifier, List<Contributor> contributors, String title, Integer year) {
        super(identifier);

        this.contributors = contributors;
        this.title = title;
        this.year = year;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    @Override
    public SourceType getType() {
        return SourceType.BOOK;
    }

}
