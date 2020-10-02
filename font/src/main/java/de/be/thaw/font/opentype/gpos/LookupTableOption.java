package de.be.thaw.font.opentype.gpos;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Options of a lookup table fetched from the lookupFlag.
 */
enum LookupTableOption {

    /**
     * When this bit is set, the last glyph in a given sequence
     * to which the cursive attachment lookup is applied,
     * will be positioned on the baseline.
     */
    RIGHT_TO_LEFT(0x0001),

    /**
     * Whether to skip over base glpyhs.
     */
    IGNORE_BASE_GLPYHS(0x0002),

    /**
     * Whether to skip over ligatures.
     */
    IGNORE_LIGATURES(0x0004),

    /**
     * Whether to skip over all combining marks.
     */
    IGNORE_MARKS(0x0008),

    /**
     * Whether the lookup table structure is extended by the
     * markFilteringSet field.
     */
    USE_MARK_FILTERING_SET(0x0010),

    /**
     * A reserved field.
     */
    RESERVED(0x00E0),

    /**
     * Whether to skip over all marks of attachment type different
     * from specified.
     */
    MARK_ATTACHMENT_TYPE(0xFF00);

    /**
     * Mask to apply on the lookupFlag to find out
     * whether the option is set.
     */
    private final int mask;

    LookupTableOption(int mask) {
        this.mask = mask;
    }

    /**
     * Get the mask to apply on the lookupFlag to find
     * out whether the option is set.
     *
     * @return mask
     */
    public int getMask() {
        return mask;
    }

    /**
     * Collect all options set by the passed lookupFlag.
     *
     * @param lookupFlag to gather set options in
     * @return options
     */
    public static Set<LookupTableOption> collect(int lookupFlag) {
        return Arrays.stream(values())
                .filter(option -> (lookupFlag & option.getMask()) != 0)
                .collect(Collectors.toSet());
    }

}
