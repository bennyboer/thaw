package de.be.thaw.reference.citation;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Citation of an already specified source in the bibliography.
 */
public class SourceCitation implements Citation {

    /**
     * ID of the source to cite.
     */
    private final String sourceID;

    /**
     * Specific location in the cited source (paragraph, page, chapter, ...).
     */
    @Nullable
    private String locator;

    /**
     * The label specified what kind of location is specified ("chapter", "page", "paragraph", ...).
     */
    @Nullable
    private String label;

    /**
     * Optional prefix.
     */
    @Nullable
    private String prefix;

    /**
     * Optional suffix.
     */
    @Nullable
    private String suffix;

    /**
     * Whether only the author should be included in the citation string.
     */
    private boolean authorOnly = false;

    /**
     * Whether the author should be suppressed in the citation string.
     */
    private boolean suppressAuthor = false;

    /**
     * Whether the same source has been referenced near to this one.
     */
    @Nullable
    private Boolean nearNote;

    public SourceCitation(String sourceID) {
        this.sourceID = sourceID;
    }

    @Override
    public String getSourceID() {
        return sourceID;
    }

    @Override
    public Optional<String> getLocator() {
        return Optional.ofNullable(locator);
    }

    @Override
    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    @Override
    public boolean isAuthorOnly() {
        return authorOnly;
    }

    @Override
    public boolean isSuppressAuthor() {
        return suppressAuthor;
    }

    @Override
    public Optional<String> getPrefix() {
        return Optional.ofNullable(prefix);
    }

    @Override
    public Optional<String> getSuffix() {
        return Optional.ofNullable(suffix);
    }

    @Override
    public Optional<Boolean> isNearNote() {
        return Optional.ofNullable(nearNote);
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setAuthorOnly(boolean authorOnly) {
        this.authorOnly = authorOnly;
    }

    public void setSuppressAuthor(boolean suppressAuthor) {
        this.suppressAuthor = suppressAuthor;
    }

    public void setNearNote(@Nullable Boolean nearNote) {
        this.nearNote = nearNote;
    }

}
