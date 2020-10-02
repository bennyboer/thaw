package de.be.thaw.font.opentype.gpos.subtable;

import de.be.thaw.font.opentype.gpos.subtable.fontunitadjust.FontUnitAdjustmentTable;

/**
 * A value record used in the GPOS sub tables.
 */
public class ValueRecord {

    /**
     * Horizontal adjustment for placement (in design units).
     */
    private final int xPlacement;

    /**
     * Vertical adjustment for placement (in design units).
     */
    private final int yPlacement;

    /**
     * Horizontal adjustment for advance (in design units).
     * Only used for horizontal layout.
     */
    private final int xAdvance;

    /**
     * Vertical adjustment for advance (in design units).
     * Only used for vertical layout.
     */
    private final int yAdvance;

    /**
     * The table defining horizontal adjustments to the placement.
     */
    private final FontUnitAdjustmentTable xPlaDevice;

    /**
     * The table defining vertical adjustments to the placement.
     */
    private final FontUnitAdjustmentTable yPlaDevice;

    /**
     * The table defining horizontal adjustments to the advance.
     */
    private final FontUnitAdjustmentTable xAdvDevice;

    /**
     * The table defining vertical adjustments to the advance.
     */
    private final FontUnitAdjustmentTable yAdvDevice;

    public ValueRecord(
            int xPlacement,
            int yPlacement,
            int xAdvance,
            int yAdvance,
            FontUnitAdjustmentTable xPlaDevice,
            FontUnitAdjustmentTable yPlaDevice,
            FontUnitAdjustmentTable xAdvDevice,
            FontUnitAdjustmentTable yAdvDevice
    ) {
        this.xPlacement = xPlacement;
        this.yPlacement = yPlacement;
        this.xAdvance = xAdvance;
        this.yAdvance = yAdvance;
        this.xPlaDevice = xPlaDevice;
        this.yPlaDevice = yPlaDevice;
        this.xAdvDevice = xAdvDevice;
        this.yAdvDevice = yAdvDevice;
    }

    public int getXPlacement() {
        return xPlacement;
    }

    public int getYPlacement() {
        return yPlacement;
    }

    public int getXAdvance() {
        return xAdvance;
    }

    public int getYAdvance() {
        return yAdvance;
    }

    public FontUnitAdjustmentTable getXPlaDevice() {
        return xPlaDevice;
    }

    public FontUnitAdjustmentTable getYPlaDevice() {
        return yPlaDevice;
    }

    public FontUnitAdjustmentTable getXAdvDevice() {
        return xAdvDevice;
    }

    public FontUnitAdjustmentTable getYAdvDevice() {
        return yAdvDevice;
    }

}
