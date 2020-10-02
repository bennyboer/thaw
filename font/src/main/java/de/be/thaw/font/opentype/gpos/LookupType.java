package de.be.thaw.font.opentype.gpos;

import java.util.HashMap;
import java.util.Map;

/**
 * Available lookup table types.
 */
public enum LookupType {

    SINGLE_ADJUSTMENT_POSITIONING(1),
    PAIR_ADJUSTMENT_POSITIONING(2),
    CURSIVE_ATTACHMENT_POSITIONING(3),
    MARK_TO_BASE_ATTACHMENT_POSITIONING(4),
    MARK_TO_LIGATURE_ATTACHMENT_POSITIONING(5),
    MARK_TO_MARK_ATTACHMENT_POSITIONING(6),
    CONTEXTUAL_POSITIONING(7),
    CHAINING_CONTEXTUAL_POSITIONING(8),
    EXTENSION_POSITIONING(9);

    /**
     * Lookup to find the type for an integer.
     */
    private static Map<Integer, LookupType> LOOKUP = new HashMap<>();

    static {
        for (LookupType type : values()) {
            LOOKUP.put(type.getTypeNumber(), type);
        }
    }

    /**
     * The lookup type number.
     */
    private final int typeNumber;

    LookupType(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    /**
     * Get the lookup type number.
     *
     * @return type number
     */
    public int getTypeNumber() {
        return typeNumber;
    }

    /**
     * Get the type for the passed integer.
     *
     * @param lookupType to get type for
     * @return type
     */
    public static LookupType forTypeNumber(int lookupType) {
        return LOOKUP.get(lookupType);
    }

}
