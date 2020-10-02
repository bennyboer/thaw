package de.be.thaw.font.opentype.gpos.subtable.classdef.format1;

import de.be.thaw.font.opentype.gpos.subtable.classdef.ClassDefTable;

/**
 * ClassDef table in format 1.
 */
public class ClassDefTableFormat1 implements ClassDefTable {

    /**
     * First glyph ID of the classValueArray.
     */
    private final int startGlyphID;

    /**
     * Array of class values (one per glyph ID).
     */
    private final int[] classValueArray;

    public ClassDefTableFormat1(int startGlyphID, int[] classValueArray) {
        this.startGlyphID = startGlyphID;
        this.classValueArray = classValueArray;
    }

    /**
     * Get the first glyph ID of the classValueArray.
     *
     * @return first glyph ID
     */
    public int getStartGlyphID() {
        return startGlyphID;
    }

    /**
     * Get the class value array.
     *
     * @return class value array
     */
    public int[] getClassValueArray() {
        return classValueArray;
    }

    @Override
    public int getClass(int glyphID) {
        return classValueArray.length > glyphID ? classValueArray[glyphID] : -1;
    }

}
