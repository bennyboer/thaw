package de.be.thaw.reference.impl;

import de.be.thaw.reference.Reference;
import de.be.thaw.reference.ReferenceType;

/**
 * Reference to an external target.
 */
public class ExternalReference implements Reference {

    /**
     * The target URL.
     */
    private final String targetUrl;

    /**
     * ID of the source of the reference.
     */
    private final String sourceID;

    /**
     * Display name of the URL.
     */
    private final String displayName;

    public ExternalReference(String targetUrl, String sourceID) {
        this(targetUrl, sourceID, targetUrl);
    }

    public ExternalReference(String targetUrl, String sourceID, String displayName) {
        this.targetUrl = targetUrl;
        this.sourceID = sourceID;
        this.displayName = displayName;
    }

    @Override
    public ReferenceType getType() {
        return ReferenceType.EXTERNAL;
    }

    @Override
    public String getSourceID() {
        return sourceID;
    }

    /**
     * Get the target URL.
     *
     * @return target URL
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * Get the display name of the URL.
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

}
