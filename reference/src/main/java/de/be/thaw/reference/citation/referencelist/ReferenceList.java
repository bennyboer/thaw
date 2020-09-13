package de.be.thaw.reference.citation.referencelist;

import java.util.List;

/**
 * Representation of a reference list.
 */
public class ReferenceList {

    /**
     * Whether to show indent that does not affect the first line.
     */
    private boolean hangingIndent = false;

    /**
     * Spacing between the entries.
     */
    private double entrySpacing = 0;

    /**
     * List of entries in the reference list.
     */
    private final List<ReferenceListEntry> entries;

    public ReferenceList(List<ReferenceListEntry> entries) {
        this.entries = entries;
    }

    /**
     * Whether to show indent affecting not the first line.
     *
     * @return hanging indent
     */
    public boolean getHangingIndent() {
        return hangingIndent;
    }

    /**
     * Whether to show indent affecting not the first line.
     *
     * @param hangingIndent to set
     */
    public void setHangingIndent(boolean hangingIndent) {
        this.hangingIndent = hangingIndent;
    }

    /**
     * Get the spacing between entries.
     *
     * @return spacing between entries
     */
    public double getEntrySpacing() {
        return entrySpacing;
    }

    /**
     * Set the entry spacing.
     *
     * @param entrySpacing to set
     */
    public void setEntrySpacing(double entrySpacing) {
        this.entrySpacing = entrySpacing;
    }

    /**
     * Get all entries in the reference list.
     *
     * @return entries
     */
    public List<ReferenceListEntry> getEntries() {
        return entries;
    }

}
