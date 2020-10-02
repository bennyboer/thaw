package de.be.thaw.font.opentype.gpos.subtable.coverage;

/**
 * A coverage table used in most sub tables.
 * It specifies all the glyphs affected by a substitution or positioning operation
 * described in the sub table.
 */
public interface CoverageTable {

    /**
     * Get the index for the passed glyph ID.
     *
     * @param glyphID to get index for
     * @return index
     */
    int getIndex(int glyphID);

}
