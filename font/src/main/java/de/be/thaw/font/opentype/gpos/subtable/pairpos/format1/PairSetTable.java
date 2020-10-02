package de.be.thaw.font.opentype.gpos.subtable.pairpos.format1;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Table of pair value records.
 */
public class PairSetTable {

    /**
     * Records in the table.
     * Ordered by glyph ID of the second glyph.
     */
    private final PairValueRecord[] records;

    /**
     * Lookup for the correct pair value record by glyph ID.
     */
    private final Map<Integer, PairValueRecord> lookup = new HashMap<>();

    public PairSetTable(PairValueRecord[] records) {
        this.records = records;

        for (PairValueRecord record : records) {
            lookup.put(record.getSecondGlyph(), record);
        }
    }

    /**
     * Get the records in the table ordered by the glyph ID
     * of the second glyph.
     *
     * @return records
     */
    public PairValueRecord[] getRecords() {
        return records;
    }

    /**
     * Get a record for the passed second glyph ID.
     *
     * @param secondGlyphID to get record for
     * @return record or an empty Optional
     */
    public Optional<PairValueRecord> getRecord(int secondGlyphID) {
        return Optional.ofNullable(lookup.get(secondGlyphID));
    }

}
