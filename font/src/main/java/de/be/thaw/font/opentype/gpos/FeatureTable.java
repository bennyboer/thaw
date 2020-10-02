package de.be.thaw.font.opentype.gpos;

/**
 * A feature table of the glyph positioning (GPOS) table.
 */
final class FeatureTable {

    /**
     * Reserved value for an offset to FeatureParams.
     */
    private final int featureParams;

    /**
     * Array of indices into the LookupList.
     */
    private final int[] lookupListIndices;

    public FeatureTable(int featureParams, int[] lookupListIndices) {
        this.featureParams = featureParams;
        this.lookupListIndices = lookupListIndices;
    }

    /**
     * Get the feature params offset.
     *
     * @return feature params offset
     */
    public int getFeatureParams() {
        return featureParams;
    }

    /**
     * Get an array of indices into the LookupList.
     *
     * @return LookupList indices
     */
    public int[] getLookupListIndices() {
        return lookupListIndices;
    }

}
