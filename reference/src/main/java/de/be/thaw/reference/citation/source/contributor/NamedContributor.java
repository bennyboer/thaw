package de.be.thaw.reference.citation.source.contributor;

/**
 * A contributor with a name.
 */
public class NamedContributor implements Contributor {

    /**
     * First name of the contributor.
     */
    private final String firstName;

    /**
     * Last name of the contributor.
     */
    private final String lastName;

    public NamedContributor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Get the first name of the contributor.
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get the last name of the contributor.
     *
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

}
