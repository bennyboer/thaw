package de.be.thaw.reference.impl;

import de.be.thaw.reference.Reference;
import de.be.thaw.reference.ReferenceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The default reference model implementation.
 */
public class DefaultReferenceModel implements ReferenceModel {

    /**
     * List of references.
     */
    private final List<Reference> references = new ArrayList<>();

    @Override
    public void addReference(Reference reference) {
        references.add(reference);
    }

    @Override
    public List<Reference> getReferences() {
        return references;
    }

}
