package de.be.thaw.reference.impl;

import de.be.thaw.reference.Reference;
import de.be.thaw.reference.ReferenceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The default reference model implementation.
 */
public class DefaultReferenceModel implements ReferenceModel {

    /**
     * List of references.
     */
    private final List<Reference> references = new ArrayList<>();

    /**
     * Reference lookup by source ID.
     */
    private final Map<String, Reference> referenceLookup = new HashMap<>();

    @Override
    public void addReference(Reference reference) {
        references.add(reference);
        referenceLookup.put(reference.getSourceID(), reference);
    }

    @Override
    public List<Reference> getReferences() {
        return references;
    }

    @Override
    public Optional<Reference> getReference(String sourceID) {
        return Optional.ofNullable(referenceLookup.get(sourceID));
    }

}
