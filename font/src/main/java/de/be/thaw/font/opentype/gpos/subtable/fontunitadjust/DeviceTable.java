package de.be.thaw.font.opentype.gpos.subtable.fontunitadjust;

/**
 * Device table for non-variable fonts.
 */
public class DeviceTable implements FontUnitAdjustmentTable {

    /**
     * Smallest size to correct (in ppem).
     */
    private final int startSize;

    /**
     * Largest size to correct (in ppem).
     */
    private final int endSize;

    /**
     * Format of the deltaValue array data (0x0001, 0x0002 or 0x0003).
     * 0x0001: LOCAL_2_BIT_DELTAS
     * 0x0002: LOCAL_4_BIT_DELTAS
     * 0x0003: LOCAL_8_BIT_DELTAS
     */
    private final DeltaFormat deltaFormat;

    /**
     * Array of delta values from the start size to the end size.
     * Index 0 is the start size and deltaValues.length - 1 is the end size adjustment value.
     */
    private final int[] deltaValues;

    public DeviceTable(int startSize, int endSize, DeltaFormat deltaFormat, int[] deltaValues) {
        this.startSize = startSize;
        this.endSize = endSize;
        this.deltaFormat = deltaFormat;
        this.deltaValues = deltaValues;
    }

    public int getStartSize() {
        return startSize;
    }

    public int getEndSize() {
        return endSize;
    }

    public DeltaFormat getDeltaFormat() {
        return deltaFormat;
    }

    public int[] getDeltaValues() {
        return deltaValues;
    }

}
