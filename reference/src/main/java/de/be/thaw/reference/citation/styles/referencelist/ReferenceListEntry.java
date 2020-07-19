package de.be.thaw.reference.citation.styles.referencelist;

/**
 * Entry to a reference list.
 */
public class ReferenceListEntry {

    /**
     * Identifier of the source we are referencing in this entry.
     */
    private final String identifier;

    /**
     * Entry of the reference list.
     */
    private final String entry;

    public ReferenceListEntry(String identifier, String entry) {
        this.identifier = identifier;
        this.entry = entry;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getEntry() {
        return entry;
    }

}
