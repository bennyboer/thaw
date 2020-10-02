package de.be.thaw.font.opentype.gpos.subtable.fontunitadjust;

/**
 * Format of the delta values stored in font unit adjustment tables.
 */
public enum DeltaFormat {

    /**
     * Signed 2-bit value, 8 values per uint16.
     */
    LOCAL_2_BIT_DELTAS(0x0001),

    /**
     * Signed 4-bit value, 4 values per uint16.
     */
    LOCAL_4_BIT_DELTAS(0x0002),

    /**
     * Signed 8-bit value, 2 values per unit16.
     */
    LOCAL_8_BIT_DELTAS(0x0003),


    /**
     * VariationInex table, contains a delta-set index pair.
     */
    VARIATION_INDEX(0x8000),


    /**
     * Reserved for future use.
     */
    RESERVED(0x7FFC);

    /**
     * Mask to test against whether the format is set.
     */
    private final int mask;

    DeltaFormat(int mask) {
        this.mask = mask;
    }

    /**
     * Get the mask to test against the set value.
     *
     * @return mask
     */
    public int getMask() {
        return mask;
    }

    /**
     * Get the format for the passed value.
     *
     * @param value to get the format of
     * @return format
     */
    public static DeltaFormat forValue(int value) {
        for (DeltaFormat format : values()) {
            if ((value & format.getMask()) != 0) {
                return format;
            }
        }

        return null;
    }

}
