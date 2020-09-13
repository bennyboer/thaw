package de.be.thaw.reference.citation;

import de.be.thaw.reference.citation.exception.MissingSourceException;
import de.be.thaw.reference.citation.referencelist.ReferenceList;

import java.util.List;

/**
 * Manager for citations
 */
public interface CitationManager {

    /**
     * Register the passed citations.
     *
     * @param citations to register
     * @return the citation string
     */
    String register(List<Citation> citations) throws MissingSourceException;

    /**
     * Check whether a source with the passed ID is available in the bibliography.
     *
     * @param sourceID of the source to check for availability
     * @return whether the source with the passed ID is available
     */
    boolean hasSource(String sourceID);

    /**
     * Build a list of bibliography entries.
     *
     * @return bibliography entries
     */
    ReferenceList buildReferenceList();

}
