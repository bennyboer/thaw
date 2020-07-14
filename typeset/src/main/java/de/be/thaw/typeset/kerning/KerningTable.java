package de.be.thaw.typeset.kerning;

import de.be.thaw.typeset.kerning.glyph.Glyph;

/**
 * Table used to kern character pairs.
 */
public interface KerningTable {

    /**
     * Initialize the table.
     *
     * @param glyphs to kern
     */
    void init(Glyph[] glyphs);

    /**
     * Get the kerning for the passed left and right glyph ID.
     *
     * @param leftGlyphID  the left glyph ID
     * @param rightGlyphID the right glyph ID
     * @return the kerning adjustment
     */
    double getKerning(int leftGlyphID, int rightGlyphID);

}
