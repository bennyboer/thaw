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

    /**
     * Add a label to node ID mapping.
     *
     * @param label  to add
     * @param nodeID of the label
     */
    void addLabel(String label, String nodeID);

    /**
     * Get the node ID for the passed label (if there is any node ID assigned to the label).
     *
     * @param label to get node ID for
     * @return node ID (or an empty Optional).
     */
    Optional<String> getNodeIDForLabel(String label);

    /**
     * Set the reference number for the passed node ID and counter.
     *
     * @param counterName name of the counter to use
     * @param nodeID      to set reference number for
     * @return the set number
     */
    int setReferenceNumber(String counterName, String nodeID);

    /**
     * Get the reference number for the passed nodeID.
     *
     * @param nodeID id of the node to get the reference number for
     * @return the reference number for the passed node ID (or -1 if not found).
     */
    int getReferenceNumber(String nodeID);

}
