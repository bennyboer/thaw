package de.be.thaw.font.opentype.gpos.subtable.coverage.format2;

/**
 * A glyph range.
 */
public final class RangeRecord {

    /**
     * First glyph ID in the range.
     */
    private final int startGlyphID;

    /**
     * Last glyph ID in the range.
     */
    private final int endGlpyhID;

    /**
     * Coverage index of the first glyph ID in range.
     */
    private final int startCoverageIndex;

    public RangeRecord(int startGlyphID, int endGlpyhID, int startCoverageIndex) {
        this.startGlyphID = startGlyphID;
        this.endGlpyhID = endGlpyhID;
        this.startCoverageIndex = startCoverageIndex;
    }

    /**
     * Get the first glyph ID in the range.
     *
     * @return first glyph ID
     */
    public int getStartGlyphID() {
        return startGlyphID;
    }

    /**
     * Get the last glyph ID in the range.
     *
     * @return last glyph ID
     */
    public int getEndGlpyhID() {
        return endGlpyhID;
    }

    /**
     * Get the coverage index of the first glyph ID in the range.
     *
     * @return coverage index of the first glyph ID
     */
    public int getStartCoverageIndex() {
        return startCoverageIndex;
    }

}
