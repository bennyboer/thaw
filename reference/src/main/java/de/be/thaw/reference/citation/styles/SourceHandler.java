package de.be.thaw.reference.citation.styles;

import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;

import java.util.Set;

/**
 * Handler for sources.
 */
public interface SourceHandler {

    /**
     * The source types this builder supports.
     *
     * @return supported source types
     */
    Set<SourceType> supports();

    /**
     * Build an reference list entry.
     *
     * @param source to build entry for
     * @return the Thaw document text formatted string
     */
    String buildReferenceListEntry(Source source) throws ReferenceBuildException;

    /**
     * Build an in-text-citation.
     *
     * @param source   to build for
     * @param position in the source (may be a page)
     * @return the Thaw document text formatted string
     */
    String buildInTextCitation(Source source, String position) throws ReferenceBuildException;

}
