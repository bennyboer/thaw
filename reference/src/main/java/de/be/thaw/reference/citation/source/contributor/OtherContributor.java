package de.be.thaw.reference.citation.source.contributor;

/**
 * Another contributor of a source that is not the author.
 */
public class OtherContributor implements Contributor {

    /**
     * Role of the contributor (translator, editor, ...).
     */
    private final String role;

    /**
     * The contributor.
     */
    private final Contributor contributor;

    public OtherContributor(Contributor contributor, String role) {
        this.contributor = contributor;
        this.role = role;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public String getRole() {
        return role;
    }

}
