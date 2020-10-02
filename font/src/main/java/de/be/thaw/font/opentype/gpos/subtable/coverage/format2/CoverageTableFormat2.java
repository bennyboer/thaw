package de.be.thaw.font.opentype.gpos.subtable.coverage.format2;

import de.be.thaw.font.opentype.gpos.subtable.coverage.CoverageTable;

/**
 * Format 2 coverage table.
 * It specifies ranges of glpyhs.
 */
public class CoverageTableFormat2 implements CoverageTable {

    /**
     * Array of glpyh ranges (ordered by startGlyphID).
     */
    private final RangeRecord[] rangeRecords;

    public CoverageTableFormat2(RangeRecord[] rangeRecords) {
        this.rangeRecords = rangeRecords;
    }

    /**
     * Get an array of glyph ranges ordered by startGlpyhID.
     *
     * @return glyph ranges.
     */
    public RangeRecord[] getRangeRecords() {
        return rangeRecords;
    }

    @Override
    public int getIndex(int glyphID) {
        for (RangeRecord record : rangeRecords) {
            if (glyphID >= record.getStartGlyphID() && glyphID <= record.getEndGlpyhID()) {
                return record.getStartCoverageIndex() + (glyphID - record.getStartGlyphID());
            }
        }

        return -1;
    }

}
