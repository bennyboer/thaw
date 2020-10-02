package de.be.thaw.font.opentype.gpos;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Language system table of a glpyh positioning (GPOS) table.
 * It identifies language-system features used to render the glyphs in a script.
 */
final class LangSysTable {

    /**
     * Offset to a reordering table.
     */
    @Nullable
    private final Integer lookupOrder;

    /**
     * Index of a feature required to the language system.
     * If no required features = 0xFFFF
     */
    private final int requiredFeatureIndex;

    /**
     * Indices into the feature list.
     */
    private final int[] featureIndices;

    public LangSysTable(@Nullable Integer lookupOrder, int requiredFeatureIndex, int[] featureIndices) {
        this.lookupOrder = lookupOrder;
        this.requiredFeatureIndex = requiredFeatureIndex;
        this.featureIndices = featureIndices;
    }

    /**
     * Get the offset to a reordering table.
     *
     * @return offset to a reordering table
     */
    public Optional<Integer> getLookupOrder() {
        return Optional.ofNullable(lookupOrder);
    }

    /**
     * Get an index of a feature required to the language system.
     * If no required features = 0xFFFF
     *
     * @return index of a feature required
     */
    public int getRequiredFeatureIndex() {
        return requiredFeatureIndex;
    }

    /**
     * Get the language-system feature indices.
     *
     * @return feature indices
     */
    public int[] getFeatureIndices() {
        return featureIndices;
    }

}
