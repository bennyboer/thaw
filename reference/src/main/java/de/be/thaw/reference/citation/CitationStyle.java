package de.be.thaw.reference.citation;

import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;
import de.be.thaw.reference.citation.styles.exception.UnsupportedSourceTypeException;
import de.be.thaw.reference.citation.styles.referencelist.ReferenceListEntry;

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
     * Get the accumulated reference list entries which were collected using the addCitation method.
     *
     * @return the reference list entries in Thaw document text format
     */
    List<ReferenceListEntry> getReferenceListEntries();

    /**
     * Build a in-text-citation and the corresponding reference list entry (if not already there) for the passed citation sources.
     * The returned string can use anything that can also be written in a *.tdt file!
     * For example "**Author name** (2020): *Isn't that a nice title*. Retrieved from #HREF, https://example.com#"
     * would be a valid return value from this method.
     *
     * @param citations to add
     * @return the in-text-citation
     * @throws UnsupportedSourceTypeException in case the source type of the citation is unsupported
     * @throws ReferenceBuildException        in case the in-text-citation or reference list entry could not be build
     */
    String addCitation(List<Citation> citations) throws UnsupportedSourceTypeException, ReferenceBuildException;

}
