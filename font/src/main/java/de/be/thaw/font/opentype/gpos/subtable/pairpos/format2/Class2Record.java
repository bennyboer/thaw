package de.be.thaw.font.opentype.gpos.subtable.pairpos.format2;

import de.be.thaw.font.opentype.gpos.subtable.ValueRecord;

/**
 * Class 2 record of the class def table.
 */
public class Class2Record {

    /**
     * Value record of the first glyph.
     */
    private final ValueRecord valueRecord1;

    /**
     * Value record of the second glyph.
     */
    private final ValueRecord valueRecord2;

    public Class2Record(ValueRecord valueRecord1, ValueRecord valueRecord2) {
        this.valueRecord1 = valueRecord1;
        this.valueRecord2 = valueRecord2;
    }

    /**
     * Get the value record of the first glyph.
     *
     * @return first glyph value record
     */
    public ValueRecord getValueRecord1() {
        return valueRecord1;
    }

    /**
     * Get the value record of the second glyph.
     *
     * @return second glyph value record
     */
    public ValueRecord getValueRecord2() {
        return valueRecord2;
    }

}
