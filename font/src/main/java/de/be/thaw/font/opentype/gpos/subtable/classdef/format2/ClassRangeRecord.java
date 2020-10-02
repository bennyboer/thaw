package de.be.thaw.font.opentype.gpos.subtable.classdef.format2;

/**
 * Record of the ClassDef table format 2.
 */
public class ClassRangeRecord {

    /**
     * First glyph ID in the range.
     */
    private final int startGlyphID;

    /**
     * Last glyph ID in the range.
     */
    private final int endGlyphID;

    /**
     * Class applied to all glyphs in the range.
     */
    private final int classValue;

    public ClassRangeRecord(int startGlyphID, int endGlyphID, int classValue) {
        this.startGlyphID = startGlyphID;
        this.endGlyphID = endGlyphID;
        this.classValue = classValue;
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
    public int getEndGlyphID() {
        return endGlyphID;
    }

    /**
     * Get the class applied to all glyphs in the range.
     *
     * @return class
     */
    public int getClassValue() {
        return classValue;
    }

}
