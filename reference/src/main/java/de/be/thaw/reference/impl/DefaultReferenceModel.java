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

    /**
     * Lookup from labels to their node ID.
     */
    private final Map<String, String> labelToNodeID = new HashMap<>();

    /**
     * Reference number lookup by node ID.
     */
    private final Map<String, Integer> referenceNumberLookup = new HashMap<>();

    /**
     * Reference counter lookup.
     */
    private final Map<String, Integer> referenceCounterLookup = new HashMap<>();

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

    @Override
    public void addLabel(String label, String nodeID) {
        labelToNodeID.put(label, nodeID);
    }

    @Override
    public Optional<String> getNodeIDForLabel(String label) {
        return Optional.ofNullable(labelToNodeID.get(label));
    }

    @Override
    public int setReferenceNumber(String counterName, String nodeID) {
        // Increase reference counter by 1 first
        referenceCounterLookup.put(counterName, referenceCounterLookup.computeIfAbsent(counterName, k -> 0) + 1);

        // Set reference number for the node ID to the new counter value
        int counter = referenceCounterLookup.get(counterName);
        referenceNumberLookup.put(nodeID, counter);

        return counter;
    }

    @Override
    public int getReferenceNumber(String nodeID) {
        return referenceNumberLookup.getOrDefault(nodeID, -1);
    }

}
