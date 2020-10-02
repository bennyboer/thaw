package de.be.thaw.font.opentype.gpos;

/**
 * A subtable of a lookup table in the glyph positioning (GPOS) table.
 */
public abstract class LookupSubTable {

    /**
     * Type of the table.
     */
    private final LookupType type;

    public LookupSubTable(LookupType type) {
        this.type = type;
    }

    /**
     * Get the type of the sub table.
     *
     * @return type
     */
    public LookupType getType() {
        return type;
    }

}
