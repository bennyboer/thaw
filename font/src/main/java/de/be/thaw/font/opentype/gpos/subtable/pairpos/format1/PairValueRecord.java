package de.be.thaw.font.opentype.gpos.subtable.pairpos.format1;

import de.be.thaw.font.opentype.gpos.subtable.ValueRecord;

/**
 * A pair value record.
 */
public class PairValueRecord {

    /**
     * Glpyh ID of the second glyph in the pair.
     * Firs one is listed in the coverage table.
     */
    private final int secondGlyph;

    /**
     * Positioning data for the first glyph in the pair.
     */
    private final ValueRecord valueRecord1;

    /**
     * Positioning data for the second glyph in the pair.
     */
    private final ValueRecord valueRecord2;

    public PairValueRecord(int secondGlyph, ValueRecord valueRecord1, ValueRecord valueRecord2) {
        this.secondGlyph = secondGlyph;
        this.valueRecord1 = valueRecord1;
        this.valueRecord2 = valueRecord2;
    }

    /**
     * Get the ID of the second glyph in the pair.
     *
     * @return second glyph ID
     */
    public int getSecondGlyph() {
        return secondGlyph;
    }

    /**
     * Get the positioning data for the first glyph in the pair.
     *
     * @return first glyph positioning data
     */
    public ValueRecord getValueRecord1() {
        return valueRecord1;
    }

    /**
     * Get the positioning data for the second glyph in the pair.
     *
     * @return second glyph positioning data
     */
    public ValueRecord getValueRecord2() {
        return valueRecord2;
    }

}
