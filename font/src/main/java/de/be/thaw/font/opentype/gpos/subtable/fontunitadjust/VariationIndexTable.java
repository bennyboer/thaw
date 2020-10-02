package de.be.thaw.font.opentype.gpos.subtable.fontunitadjust;

/**
 * VariationIndex table for variable fonts.
 */
public class VariationIndexTable implements FontUnitAdjustmentTable {

    /**
     * A delta-set outer index. Used to select an item
     * variation data subtable within the item variation store.
     */
    private final int deltaSetOuterIndex;

    /**
     * A delta-set inner index. Used to select a delta-set row
     * within an item variation data subtable.
     */
    private final int deltaSetInnerIndex;

    /**
     * Delta format for 0x8000.
     */
    private final DeltaFormat deltaFormat;

    public VariationIndexTable(int deltaSetOuterIndex, int deltaSetInnerIndex, DeltaFormat deltaFormat) {
        this.deltaSetOuterIndex = deltaSetOuterIndex;
        this.deltaSetInnerIndex = deltaSetInnerIndex;
        this.deltaFormat = deltaFormat;
    }

    public int getDeltaSetOuterIndex() {
        return deltaSetOuterIndex;
    }

    public int getDeltaSetInnerIndex() {
        return deltaSetInnerIndex;
    }

    public DeltaFormat getDeltaFormat() {
        return deltaFormat;
    }

}
