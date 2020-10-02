package de.be.thaw.font.opentype.gpos;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * LookupTable in a glyph positioning (GPOS) table.
 */
final class LookupTable {

    /**
     * Type of the lookup of the GPOS table.
     * The GPOS table supports nine different lookup types.
     */
    private final int lookupType;

    /**
     * Lookup qualifiers.
     */
    private final int lookupFlag;

    /**
     * Set of options parsed from the lookupFlag.
     */
    private final Set<LookupTableOption> options;

    /**
     * Available sub tables in the table.
     */
    private final LookupSubTable[] subTables;

    /**
     * Index (base 0) into GDEF mark glyph sets structure.
     * Set if options contain the USE_MARK_FILTERING_SET option.
     */
    @Nullable
    private final Integer markFilteringSet;

    public LookupTable(
            int lookupType,
            int lookupFlag,
            Set<LookupTableOption> options,
            LookupSubTable[] subTables,
            @Nullable Integer markFilteringSet
    ) {
        this.lookupType = lookupType;
        this.lookupFlag = lookupFlag;
        this.options = options;
        this.subTables = subTables;
        this.markFilteringSet = markFilteringSet;
    }

    /**
     * Get the type of the lookup.
     *
     * @return lookup type
     */
    public int getLookupType() {
        return lookupType;
    }

    /**
     * Get the lookup qualifiers.
     *
     * @return flag
     */
    public int getLookupFlag() {
        return lookupFlag;
    }

    /**
     * Get the options set in the lookup flag.
     *
     * @return options
     */
    public Set<LookupTableOption> getOptions() {
        return options;
    }

    /**
     * Get the available sub tables.
     *
     * @return sub tables
     */
    public LookupSubTable[] getSubTables() {
        return subTables;
    }

    /**
     * Get the index (base 0) into GDEF mark glyph sets structure.
     *
     * @return mark filtering set
     */
    public Optional<Integer> getMarkFilteringSet() {
        return Optional.ofNullable(markFilteringSet);
    }

}
