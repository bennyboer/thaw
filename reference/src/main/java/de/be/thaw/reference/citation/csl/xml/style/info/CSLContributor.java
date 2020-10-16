package de.be.thaw.reference.citation.csl.xml.style.info;

import org.jetbrains.annotations.Nullable;

/**
 * Contributor of a CSL style info.
 */
public class CSLContributor {

    /**
     * Name of the contributor.
     */
    private String name;

    /**
     * E-Mail of the contributor.
     */
    @Nullable
    private String email;

    /**
     * URI of the contributor.
     */
    @Nullable
    private String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    @Nullable
    public String getUri() {
        return uri;
    }

    public void setUri(@Nullable String uri) {
        this.uri = uri;
    }

}
