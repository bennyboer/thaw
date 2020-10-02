package de.be.thaw.font.opentype.gpos.subtable.coverage.format1;

import de.be.thaw.font.opentype.gpos.subtable.coverage.CoverageTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Format 1 coverage table.
 * It specifies individual glyph indices.
 */
public class CoverageTableFormat1 implements CoverageTable {

    /**
     * Array of glyph IDs in numerical order.
     */
    private final int[] glyphIDs;

    /**
     * Lookup for the index of the glyph IDs.
     */
    private final Map<Integer, Integer> indexLookup = new HashMap<>();

    public CoverageTableFormat1(int[] glyphIDs) {
        this.glyphIDs = glyphIDs;

        for (int i = 0; i < glyphIDs.length; i++) {
            indexLookup.put(glyphIDs[i], i);
        }
    }

    /**
     * Get the glyph IDs in numerical order.
     *
     * @return glyph IDs
     */
    public int[] getGlyphIDs() {
        return glyphIDs;
    }

    @Override
    public int getIndex(int glyphID) {
        return indexLookup.getOrDefault(glyphID, -1);
    }

}
