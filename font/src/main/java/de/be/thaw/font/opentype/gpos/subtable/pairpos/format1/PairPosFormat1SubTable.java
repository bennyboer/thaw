package de.be.thaw.font.opentype.gpos.subtable.pairpos.format1;

import de.be.thaw.font.opentype.gpos.LookupSubTable;
import de.be.thaw.font.opentype.gpos.LookupType;
import de.be.thaw.font.opentype.gpos.subtable.coverage.CoverageTable;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.PairPosSubTable;

import java.util.Optional;

/**
 * Pair adjustment positioning sub table of format 1.
 */
public class PairPosFormat1SubTable extends LookupSubTable implements PairPosSubTable {

    /**
     * The tables coverage table.
     */
    private final CoverageTable coverageTable;

    /**
     * Whether the first glyph in the pair has a value record.
     * Otherwise is set to 0.
     */
    private final int valueFormat1;

    /**
     * Whether the second glyph in the pair has a value record.
     * Otherwise is set to 0.
     */
    private final int valueFormat2;

    /**
     * Pair set tables used to lookup the position adjustments.
     * Ordered by the coverage index.
     */
    private final PairSetTable[] pairSetTables;

    public PairPosFormat1SubTable(CoverageTable coverageTable, int valueFormat1, int valueFormat2, PairSetTable[] pairSetTables) {
        super(LookupType.PAIR_ADJUSTMENT_POSITIONING);

        this.coverageTable = coverageTable;
        this.valueFormat1 = valueFormat1;
        this.valueFormat2 = valueFormat2;
        this.pairSetTables = pairSetTables;
    }

    /**
     * Get the tables coverage.
     *
     * @return coverage
     */
    public CoverageTable getCoverageTable() {
        return coverageTable;
    }

    /**
     * Get whether the first glyph in the pair has a value record.
     * Otherwise is set to 0.
     *
     * @return valueFormat1
     */
    public int getValueFormat1() {
        return valueFormat1;
    }

    /**
     * Get whether the second glyph in the pair has a value record.
     * Otherwise is set to 0.
     *
     * @return valueFormat2
     */
    public int getValueFormat2() {
        return valueFormat2;
    }

    /**
     * Get the pair positioning set tables to lookup position adjustments.
     *
     * @return pair set tables
     */
    public PairSetTable[] getPairSetTables() {
        return pairSetTables;
    }

    @Override
    public Optional<PairPosAdjustment> getAdjustment(int leftGlyphID, int rightGlyphID) {
        int index = getCoverageTable().getIndex(leftGlyphID);
        if (index > -1) {
            Optional<PairValueRecord> optionalRecord = getPairSetTables()[index].getRecord(rightGlyphID);
            if (optionalRecord.isPresent()) {
                PairValueRecord record = optionalRecord.orElseThrow();

                return Optional.of(new PairPosAdjustment(record.getValueRecord1(), record.getValueRecord2()));
            }
        }

        return Optional.empty();
    }

}
