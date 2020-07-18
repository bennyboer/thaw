package de.be.thaw.reference.citation.source.contributor;

/**
 * Author as a contributor to a source.
 */
public class Author implements Contributor {

    /**
     * Contributor that is the author.
     */
    private final Contributor contributor;

    public Author(Contributor contributor) {
        this.contributor = contributor;
    }

    public Contributor getContributor() {
        return contributor;
    }

}
