package de.be.thaw.font.opentype.gpos.subtable.pairpos.format2;

import de.be.thaw.font.opentype.gpos.LookupSubTable;
import de.be.thaw.font.opentype.gpos.LookupType;
import de.be.thaw.font.opentype.gpos.subtable.classdef.ClassDefTable;
import de.be.thaw.font.opentype.gpos.subtable.coverage.CoverageTable;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.PairPosSubTable;

import java.util.Optional;

/**
 * Pair positioning sub table of the glyph positioning (GPOS) table in format 2.
 */
public class PairPosFormat2SubTable extends LookupSubTable implements PairPosSubTable {

    /**
     * Coverage table giving the proper indices for the left glyph ID.
     */
    private final CoverageTable coverageTable;

    /**
     * Value record format for the first glyph in a pair.
     */
    private final int valueFormat1;

    /**
     * Value record format for the second glyph in a pair.
     */
    private final int valueFormat2;

    /**
     * ClassDef table for the first glyph of the pair.
     */
    private final ClassDefTable classDef1Table;

    /**
     * ClassDef table for the second glyph of the pair.
     */
    private final ClassDefTable classDef2Table;

    /**
     * Number of classes in the classDef1 table.
     */
    private final int class1Count;

    /**
     * Number of classes in the classDef2 table.
     */
    private final int class2Count;

    /**
     * Available class 1 records.
     * Ordered by classes in classDef1.
     */
    private final Class1Record[] class1Records;

    public PairPosFormat2SubTable(
            CoverageTable coverageTable,
            int valueFormat1,
            int valueFormat2,
            ClassDefTable classDef1Table,
            ClassDefTable classDef2Table,
            int class1Count,
            int class2Count,
            Class1Record[] class1Records
    ) {
        super(LookupType.PAIR_ADJUSTMENT_POSITIONING);

        this.coverageTable = coverageTable;
        this.valueFormat1 = valueFormat1;
        this.valueFormat2 = valueFormat2;
        this.classDef1Table = classDef1Table;
        this.classDef2Table = classDef2Table;
        this.class1Count = class1Count;
        this.class2Count = class2Count;
        this.class1Records = class1Records;
    }

    public CoverageTable getCoverageTable() {
        return coverageTable;
    }

    public int getValueFormat1() {
        return valueFormat1;
    }

    public int getValueFormat2() {
        return valueFormat2;
    }

    public ClassDefTable getClassDef1Table() {
        return classDef1Table;
    }

    public ClassDefTable getClassDef2Table() {
        return classDef2Table;
    }

    public int getClass1Count() {
        return class1Count;
    }

    public int getClass2Count() {
        return class2Count;
    }

    public Class1Record[] getClass1Records() {
        return class1Records;
    }

    @Override
    public Optional<PairPosAdjustment> getAdjustment(int leftGlyphID, int rightGlyphID) {
        // Fetch classes for the left and right glyph first
        int leftClass = getClassDef1Table().getClass(leftGlyphID);
        int rightClass = getClassDef2Table().getClass(rightGlyphID);

        // Fetch value records for the two classes (if any)
        if (leftClass > -1 && rightClass > -1) {
            Class1Record class1Record = getClass1Records()[leftClass];
            Class2Record class2Record = class1Record.getClass2Records()[rightClass];

            return Optional.of(new PairPosAdjustment(class2Record.getValueRecord1(), class2Record.getValueRecord2()));
        }

        return Optional.empty();
    }

}
