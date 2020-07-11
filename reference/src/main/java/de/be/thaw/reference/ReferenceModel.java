package de.be.thaw.reference;

import java.util.List;
import java.util.Optional;

/**
 * Model holding references (internal and external to a document).
 * With references we mean linking of document parts to other document parts:
 * e. g. citations, hyperlinks to remote sources (websites), internal links to
 * parts of the document.
 */
public interface ReferenceModel {

    /**
     * Add a reference to the model.
     *
     * @param reference to add
     */
    void addReference(Reference reference);

    /**
     * Get a list of references currently managed by the model.
     *
     * @return references
     */
    List<Reference> getReferences();

    /**
     * Get the reference for the passed source ID (if any).
     *
     * @param sourceID to get reference for
     * @return reference (or empty optional)
     */
    Optional<Reference> getReference(String sourceID);

}
