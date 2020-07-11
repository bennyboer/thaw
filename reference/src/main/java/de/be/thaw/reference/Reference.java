package de.be.thaw.reference;

/**
 * A reference.
 */
public interface Reference {

    /**
     * Get the reference type.
     *
     * @return type of the reference
     */
    ReferenceType getType();

    /**
     * Get the ID of the source.
     *
     * @return source ID
     */
    String getSourceID();

}
