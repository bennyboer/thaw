package de.be.thaw.reference.citation.referencelist;

/**
 * Entry of a reference list.
 */
public class ReferenceListEntry {

    /**
     * The source ID this reference list entry represents.
     */
    private final String sourceID;

    /**
     * Text in the Thaw document text format.
     */
    private final String text;

    public ReferenceListEntry(String sourceID, String text) {
        this.sourceID = sourceID;
        this.text = text;
    }

    /**
     * Get the source ID this entry represents.
     *
     * @return source ID
     */
    public String getSourceID() {
        return sourceID;
    }

    /**
     * Get the text in the Thaw document text format to display.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

}
