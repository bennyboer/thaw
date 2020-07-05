package de.be.thaw.info.model.author;

/**
 * Author details defined in the Thaw document info file.
 */
public class Author {

    /**
     * The authors name.
     */
    private final String name;

    /**
     * E-Mail address of the author.
     */
    private final String email;

    public Author(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

}
