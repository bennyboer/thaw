package de.be.thaw.reference.citation;

import java.util.Optional;

/**
 * A citation representation.
 */
public interface Citation {

    /**
     * Get the ID of the source to cite.
     *
     * @return the source ID
     */
    String getSourceID();

    /**
     * Get the locator of the citation (for example the page number to cite).
     *
     * @return the locator
     */
    Optional<String> getLocator();

    /**
     * Get a label indicating whether the locator is a page, chapter, ...
     *
     * @return the label
     */
    Optional<String> getLabel();

    /**
     * Whether only the author name should be included in the citation output.
     *
     * @return whether author only
     */
    boolean isAuthorOnly();

    /**
     * Whether the author should be suppressed in the citation output.
     *
     * @return suppress author in citation output
     */
    boolean isSuppressAuthor();

    /**
     * Get an optional prefix of the citation.
     *
     * @return prefix
     */
    Optional<String> getPrefix();

    /**
     * Get an optional suffix of the citation.
     *
     * @return suffix
     */
    Optional<String> getSuffix();

    /**
     * Whether there is another reference to the same source before this one.
     *
     * @return whether there is another near reference to the same source
     */
    Optional<Boolean> isNearNote();

}
