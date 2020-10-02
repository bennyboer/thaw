package de.be.thaw.font.opentype.gpos.subtable.classdef;

/**
 * ClassDefTable for the format 2 pair positioning table.
 */
public interface ClassDefTable {

    /**
     * Get the class for the passed glyph ID or -1.
     *
     * @param glyphID to get class for
     * @return class
     */
    int getClass(int glyphID);

}
