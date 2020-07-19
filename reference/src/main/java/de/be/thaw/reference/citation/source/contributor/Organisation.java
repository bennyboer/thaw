package de.be.thaw.reference.citation.source.contributor;

/**
 * An organisation as contributor.
 */
public class Organisation implements Contributor {

    /**
     * Name of the organisation.
     */
    private final String name;

    public Organisation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
