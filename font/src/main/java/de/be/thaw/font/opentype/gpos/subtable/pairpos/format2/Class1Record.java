package de.be.thaw.font.opentype.gpos.subtable.pairpos.format2;

/**
 * The Class1Record of the class def table.
 */
public class Class1Record {

    /**
     * Class 2 records in the record.
     */
    private final Class2Record[] class2Records;

    public Class1Record(Class2Record[] class2Records) {
        this.class2Records = class2Records;
    }

    public Class2Record[] getClass2Records() {
        return class2Records;
    }

}
