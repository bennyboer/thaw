package de.be.thaw.font.opentype.gpos.subtable.classdef.format2;

import de.be.thaw.font.opentype.gpos.subtable.classdef.ClassDefTable;

/**
 * ClassDef table in format 2.
 */
public class ClassDefTableFormat2 implements ClassDefTable {

    /**
     * Array of class change records ordered
     * by startGlyphID.
     */
    private final ClassRangeRecord[] records;

    public ClassDefTableFormat2(ClassRangeRecord[] records) {
        this.records = records;
    }

    /**
     * Get an array of class change records.
     *
     * @return class change records
     */
    public ClassRangeRecord[] getRecords() {
        return records;
    }

    @Override
    public int getClass(int glyphID) {
        for (ClassRangeRecord record : getRecords()) {
            if (record.getStartGlyphID() <= glyphID && record.getEndGlyphID() >= glyphID) {
                return record.getClassValue();
            }
        }

        return -1;
    }

}
