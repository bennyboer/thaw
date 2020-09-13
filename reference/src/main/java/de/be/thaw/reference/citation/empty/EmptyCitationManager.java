package de.be.thaw.reference.citation.empty;

import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.CitationManager;
import de.be.thaw.reference.citation.referencelist.ReferenceList;

import java.util.List;

/**
 * An empty citation manager.
 * This one is used when there is no bibliograpy specified.
 */
public class EmptyCitationManager implements CitationManager {

    @Override
    public String register(List<Citation> citations) {
        throw new UnsupportedOperationException("Please specify a bibliography file in the info file in order to use citations");
    }

    @Override
    public boolean hasSource(String sourceID) {
        return false;
    }

    @Override
    public ReferenceList buildReferenceList() {
        throw new UnsupportedOperationException("Please specify a bibliography file in the info file in order to use citations");
    }

}
