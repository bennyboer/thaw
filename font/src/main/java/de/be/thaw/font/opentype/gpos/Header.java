package de.be.thaw.font.opentype.gpos;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Header of the glyph positioning table (GPOS) table.
 */
final class Header {

    /**
     * Major version of the table.
     */
    private final int majorVersion;

    /**
     * Minor version of the table.
     */
    private final int minorVersion;

    /**
     * Offset to the ScriptList table.
     */
    private final int scriptListOffset;

    /**
     * Offset to the FeatureList table.
     */
    private final int featureListOffset;

    /**
     * Offset to the LookupList table.
     */
    private final int lookupListOffset;

    /**
     * Offset to the FeatureVariations table.
     * Depends on version: Version 1.0 does not have this.
     */
    @Nullable
    private final Long featureVariationsOffset;

    public Header(
            int majorVersion,
            int minorVersion,
            int scriptListOffset,
            int featureListOffset,
            int lookupListOffset,
            @Nullable Long featureVariationsOffset
    ) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.scriptListOffset = scriptListOffset;
        this.featureListOffset = featureListOffset;
        this.lookupListOffset = lookupListOffset;
        this.featureVariationsOffset = featureVariationsOffset;
    }

    /**
     * Get the major version of the table.
     *
     * @return major version
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Get the minor version of the table.
     *
     * @return minor version
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Get the offset to the ScriptList table.
     *
     * @return ScriptList table offset
     */
    public int getScriptListOffset() {
        return scriptListOffset;
    }

    /**
     * Get the offset to the FeatureList table.
     *
     * @return FeatureList table offset
     */
    public int getFeatureListOffset() {
        return featureListOffset;
    }

    /**
     * Get the offset to the LookupList table.
     *
     * @return LookupList table offset.
     */
    public int getLookupListOffset() {
        return lookupListOffset;
    }

    /**
     * Get the offset to the FeatureVariations table.
     *
     * @return FeatureVariations table (or empty optional)
     */
    public Optional<Long> getFeatureVariationsOffset() {
        return Optional.ofNullable(featureVariationsOffset);
    }

}
