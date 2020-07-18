package de.be.thaw.reference.citation;

import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;
import de.be.thaw.reference.citation.styles.exception.UnsupportedSourceTypeException;

import java.util.List;

/**
 * Representation of a citation style.
 */
public interface CitationStyle {

    /**
     * Name of the citation style.
     *
     * @return name
     */
    String getName();

    /**
     * Build a reference list entry for the passed source.
     * The returned string can use anything that can also be written in a *.tdt file!
     * For example "**Author name** (2020): *Isn't that a nice title*. Retrieved from #HREF, https://example.com#"
     * would be a valid return value from this method.
     *
     * @param source to build reference list entry for
     * @return the reference list entry
     */
    String buildReferenceListEntry(Source source) throws UnsupportedSourceTypeException, ReferenceBuildException;

    /**
     * Build an in-text-citation for the passed sources.
     *
     * @param sources   to build in-text-citation for.
     * @param positions of the citation in the sources
     * @return the in-text-citation
     */
    String buildInTextCitation(List<Source> sources, List<String> positions) throws UnsupportedSourceTypeException, ReferenceBuildException;

}
