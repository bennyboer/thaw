package de.be.thaw.reference.impl;

import de.be.thaw.reference.Reference;
import de.be.thaw.reference.ReferenceType;

import java.util.Optional;

/**
 * An internal reference (inside the document).
 */
public class InternalReference implements Reference {

    /**
     * ID of the source of the reference.
     */
    private final String sourceID;

    /**
     * ID of the target of the reference.
     */
    private final String targetID;

    /**
     * String to prefix the reference number with.
     */
    private final String prefix;

    /**
     * The name of the counter to use.
     */
    private final String counterName;

    public InternalReference(String sourceID, String targetID) {
        this(sourceID, targetID, null);
    }

    public InternalReference(String sourceID, String targetID, String counterName) {
        this(sourceID, targetID, counterName, null);
    }

    public InternalReference(String sourceID, String targetID, String counterName, String prefix) {
        this.sourceID = sourceID;
        this.targetID = targetID;
        this.prefix = prefix;

        if (counterName != null) {
            this.counterName = counterName;
        } else {
            this.counterName = "DEFAULT";
        }
    }

    @Override
    public ReferenceType getType() {
        return ReferenceType.INTERNAL;
    }

    @Override
    public String getSourceID() {
        return sourceID;
    }

    /**
     * Get the target ID of the reference.
     *
     * @return target ID
     */
    public String getTargetID() {
        return targetID;
    }

    /**
     * Get the prefix to the reference (if any).
     *
     * @return prefix
     */
    public Optional<String> getPrefix() {
        return Optional.ofNullable(prefix);
    }

    /**
     * Get the name of the counter to use.
     *
     * @return counter name
     */
    public String getCounterName() {
        return counterName;
    }

}
