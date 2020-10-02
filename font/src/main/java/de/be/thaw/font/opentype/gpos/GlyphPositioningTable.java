package de.be.thaw.font.opentype.gpos;

import de.be.thaw.font.opentype.gpos.subtable.ValueRecord;
import de.be.thaw.font.opentype.gpos.subtable.classdef.ClassDefTable;
import de.be.thaw.font.opentype.gpos.subtable.classdef.format1.ClassDefTableFormat1;
import de.be.thaw.font.opentype.gpos.subtable.classdef.format2.ClassDefTableFormat2;
import de.be.thaw.font.opentype.gpos.subtable.classdef.format2.ClassRangeRecord;
import de.be.thaw.font.opentype.gpos.subtable.coverage.CoverageTable;
import de.be.thaw.font.opentype.gpos.subtable.coverage.format1.CoverageTableFormat1;
import de.be.thaw.font.opentype.gpos.subtable.coverage.format2.CoverageTableFormat2;
import de.be.thaw.font.opentype.gpos.subtable.coverage.format2.RangeRecord;
import de.be.thaw.font.opentype.gpos.subtable.fontunitadjust.DeltaFormat;
import de.be.thaw.font.opentype.gpos.subtable.fontunitadjust.DeviceTable;
import de.be.thaw.font.opentype.gpos.subtable.fontunitadjust.FontUnitAdjustmentTable;
import de.be.thaw.font.opentype.gpos.subtable.fontunitadjust.VariationIndexTable;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.PairPosSubTable;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.format1.PairPosFormat1SubTable;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.format1.PairSetTable;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.format1.PairValueRecord;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.format2.Class1Record;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.format2.Class2Record;
import de.be.thaw.font.opentype.gpos.subtable.pairpos.format2.PairPosFormat2SubTable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Implementation of the GPOS (glyph positioning table).
 */
public class GlyphPositioningTable {

    /**
     * Logger of the class.
     */
    private static final Logger LOGGER = Logger.getLogger(GlyphPositioningTable.class.getSimpleName());

    /**
     * Tag of the GPOS table.
     */
    public static final String TAG = "GPOS";

    /**
     * Header of the table.
     */
    private Header header;

    /**
     * Lookup for script tables by their tag.
     * This lookup identifies all the scripts and language systems in the font that use glyph positioning.
     */
    private Map<String, ScriptTable> scriptTableLookup;

    /**
     * Lookup for feature tables by their index.
     */
    private FeatureTable[] featureTables;

    /**
     * An array of tags for feature tables that is accessible by index.
     */
    private String[] featureTableTags;

    /**
     * Lookup for lookup tables by their index.
     */
    private LookupTable[] lookupTables;

    /**
     * Cache for already fetched kerning pairs.
     */
    private Map<Long, Integer> kerningCache = new HashMap<>();

    /**
     * Create the GPOS table from the passed bytes.
     *
     * @param inputStream of the table data
     * @throws IOException if the table could not be parsed
     */
    public GlyphPositioningTable(InputStream inputStream) throws IOException {
        read(inputStream);
    }

    /**
     * Get kerning for the passed glyph pair.
     *
     * @param leftGlyphID     left glyph ID
     * @param rightGlyphID    right glyph ID
     * @param scriptTags      script tags applicable to the passed glyphs
     * @param enabledFeatures set of enabled features
     * @return kerning (in design units)
     */
    public int getKerning(int leftGlyphID, int rightGlyphID, String[] scriptTags, Set<String> enabledFeatures) {
        long cacheKey = Integer.toUnsignedLong(leftGlyphID) | Integer.toUnsignedLong(rightGlyphID) << 32;

        Integer cachedKerningValue = kerningCache.get(cacheKey);
        if (cachedKerningValue != null) {
            return cachedKerningValue;
        }

        int kerningValue = 0;

        String scriptTag = chooseScriptTag(scriptTags);
        ScriptTable scriptTable = scriptTableLookup.get(scriptTag);
        if (scriptTable != null) {
            List<LangSysTable> langSysTables = fetchLangSysTables(scriptTable);
            List<FeatureTable> featureTables = fetchFeatureTables(langSysTables, enabledFeatures);

            for (FeatureTable featureTable : featureTables) {
                for (int lookupListIndex : featureTable.getLookupListIndices()) {
                    LookupTable lookupTable = lookupTables[lookupListIndex];
                    LookupType type = LookupType.forTypeNumber(lookupTable.getLookupType());

                    if (type == LookupType.PAIR_ADJUSTMENT_POSITIONING) {
                        for (LookupSubTable subTable : lookupTable.getSubTables()) {
                            PairPosSubTable pairPosSubTable = (PairPosSubTable) subTable;

                            Optional<PairPosSubTable.PairPosAdjustment> optionalAdjustment = pairPosSubTable.getAdjustment(leftGlyphID, rightGlyphID);
                            if (optionalAdjustment.isPresent()) {
                                PairPosSubTable.PairPosAdjustment adjustment = optionalAdjustment.orElseThrow();

                                kerningValue += adjustment.getLeft().getXAdvance();
                            }
                        }
                    }
                }
            }
        }

        kerningCache.put(cacheKey, kerningValue);

        return kerningValue;
    }

    /**
     * Fetch all feature tables for the passed LangSys tables.
     *
     * @param langSysTables   to fetch feature tables for
     * @param enabledFeatures set of enabled features
     * @return feature tables
     */
    private List<FeatureTable> fetchFeatureTables(List<LangSysTable> langSysTables, Set<String> enabledFeatures) {
        if (langSysTables.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch a list of feature tables
        List<FeatureTable> result = new ArrayList<>();
        for (LangSysTable langSysTable : langSysTables) {
            // Add the required feature table (if any)
            int requiredFeatureIndex = langSysTable.getRequiredFeatureIndex();
            if (requiredFeatureIndex != 0xffff && requiredFeatureIndex < featureTables.length) {
                result.add(featureTables[requiredFeatureIndex]);
            }

            // Add remaining language-system table features
            for (int featureIndex : langSysTable.getFeatureIndices()) {
                if (featureIndex < featureTables.length && (enabledFeatures == null || enabledFeatures.contains(featureTableTags[featureIndex]))) {
                    result.add(featureTables[featureIndex]);
                }
            }
        }

        return result;
    }

    /**
     * Fetch all LangSys tables for the passed script tag.
     *
     * @param scriptTable to fetch tables for
     * @return LangSys tables
     */
    private List<LangSysTable> fetchLangSysTables(ScriptTable scriptTable) {
        List<LangSysTable> result = new ArrayList<>(scriptTable.getTableLookup().values());

        scriptTable.getDefaultLangSysTable().ifPresent(result::add);

        return result;
    }

    /**
     * Choose the best suited script tag among the passed.
     *
     * @param tags to choose from
     * @return the best script tag
     */
    private String chooseScriptTag(String[] tags) {
        if (tags.length == 1) {
            return tags[0];
        }

        for (String tag : tags) {
            ScriptTable scriptTable = scriptTableLookup.get(tag);
            if (scriptTable != null) {
                return tag;
            }
        }

        return tags[0];
    }

    /**
     * Read the table from the given bytes.
     *
     * @param inputStream of the table data
     */
    private void read(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(inputStream);
        dis.mark(Integer.MAX_VALUE); // Mark the beginning of the stream

        header = readHeader(dis);

        readScriptTables(dis);
        readFeatureTables(dis);
        readLookupTables(dis);
    }

    /**
     * Read the GPOS table header.
     *
     * @param dis stream to read data from
     * @return GPOS table header
     */
    private Header readHeader(DataInputStream dis) throws IOException {
        int majorVersion = dis.readUnsignedShort();
        int minorVersion = dis.readUnsignedShort();

        int scriptListOffset = dis.readUnsignedShort();
        int featureListOffset = dis.readUnsignedShort();
        int lookupListOffset = dis.readUnsignedShort();

        Long featureVariationsOffset = null;
        if (minorVersion > 0) {
            featureVariationsOffset = Integer.toUnsignedLong(dis.readInt());
        }

        return new Header(
                majorVersion,
                minorVersion,
                scriptListOffset,
                featureListOffset,
                lookupListOffset,
                featureVariationsOffset
        );
    }

    /**
     * Read all script records from the passed input stream.
     *
     * @param dis the input stream
     * @return script records
     * @throws IOException in case the script records could not be read
     */
    private ScriptRecord[] readScriptRecords(DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(header.getScriptListOffset());

        int scriptCount = dis.readUnsignedShort();

        ScriptRecord[] records = new ScriptRecord[scriptCount];

        for (int i = 0; i < scriptCount; i++) {
            records[i] = readScriptRecord(dis);
        }

        return records;
    }

    /**
     * Read a single script record from the stream.
     *
     * @param dis the input stream
     * @return script record
     * @throws IOException in case the script record could not be read
     */
    private ScriptRecord readScriptRecord(DataInputStream dis) throws IOException {
        byte[] scriptTagBuffer = new byte[4];
        dis.read(scriptTagBuffer);

        String scriptTag = new String(scriptTagBuffer, StandardCharsets.ISO_8859_1);
        int scriptOffset = dis.readUnsignedShort();

        return new ScriptRecord(scriptTag, scriptOffset);
    }

    /**
     * Read all language system records from the current input stream.
     *
     * @param dis input stream to read records from
     * @return language-system records
     * @throws IOException in case the records could not be read
     */
    private LangSysRecord[] readLangSysRecords(DataInputStream dis) throws IOException {
        int langSysCount = dis.readUnsignedShort();

        LangSysRecord[] langSysRecords = new LangSysRecord[langSysCount];

        for (int i = 0; i < langSysCount; i++) {
            langSysRecords[i] = readLangSysRecord(dis);
        }

        return langSysRecords;
    }

    /**
     * Read a language-system record from the input stream.
     *
     * @param dis input stream
     * @return language-system record
     * @throws IOException in case the language system record could not be read
     */
    private LangSysRecord readLangSysRecord(DataInputStream dis) throws IOException {
        byte[] tagBuffer = new byte[4];
        dis.read(tagBuffer);

        String tag = new String(tagBuffer, StandardCharsets.ISO_8859_1);
        int offset = dis.readUnsignedShort();

        return new LangSysRecord(tag, offset);
    }

    /**
     * Read a script table for the given record and input stream.
     *
     * @param record to read table for
     * @param dis    input stream
     * @return script table
     * @throws IOException in case the script table could not be read
     */
    private ScriptTable readScriptTable(ScriptRecord record, DataInputStream dis) throws IOException {
        dis.reset(); // Jump to the beginning of the GPOS table
        dis.skipBytes(header.getScriptListOffset() + record.getOffset()); // Jump to the beginning of the script table to parse

        int defaultLangSysOffset = dis.readUnsignedShort();

        LangSysRecord[] langSysRecords = readLangSysRecords(dis);

        // Parse default language-system table
        dis.reset(); // Jump to the beginning of the GPOS table
        dis.skipBytes(header.getScriptListOffset() + record.getOffset() + defaultLangSysOffset); // Jump to the default language-system table

        LangSysTable defaultLangSysTable = readLangSysTable(dis);

        // Parse other language-system tables
        Map<String, LangSysTable> tables = new HashMap<>();
        for (LangSysRecord langSysRecord : langSysRecords) {
            dis.reset(); // Jump to the beginning of the GPOS table
            dis.skipBytes(header.getScriptListOffset() + record.getOffset() + langSysRecord.getOffset()); // Jump to the beginning of the LangSys table to parse

            tables.put(langSysRecord.getTag(), readLangSysTable(dis));
        }

        return new ScriptTable(defaultLangSysTable, tables);
    }

    /**
     * Read the ScriptList tables from the provided stream.
     *
     * @param dis input stream
     * @throws IOException in case the ScriptList could not be read
     */
    private void readScriptTables(DataInputStream dis) throws IOException {
        ScriptRecord[] records = readScriptRecords(dis);

        Map<String, ScriptTable> result = new HashMap<>();
        for (ScriptRecord record : records) {
            result.put(record.getTag(), readScriptTable(record, dis));
        }

        scriptTableLookup = result;
    }

    /**
     * Read a LangSysTable from the current input stream.
     *
     * @param dis stream to read from
     * @return LangSysTable
     * @throws IOException in case the table could not be read
     */
    private LangSysTable readLangSysTable(DataInputStream dis) throws IOException {
        dis.readUnsignedShort(); // Skip lookupOrder (not yet used)

        int requiredFeatureIndex = dis.readUnsignedShort();

        int featureIndexCount = dis.readUnsignedShort();
        int[] featureIndices = new int[featureIndexCount];
        for (int i = 0; i < featureIndexCount; i++) {
            featureIndices[i] = dis.readUnsignedShort();
        }

        return new LangSysTable(null, requiredFeatureIndex, featureIndices);
    }

    /**
     * Read all feature records in the passed input stream.
     *
     * @param dis input stream
     * @return feature records
     * @throws IOException in case the records could not be read
     */
    private FeatureRecord[] readFeatureRecords(DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(header.getFeatureListOffset());

        int featureCount = dis.readUnsignedShort();

        FeatureRecord[] records = new FeatureRecord[featureCount];
        for (int i = 0; i < featureCount; i++) {
            records[i] = readFeatureRecord(dis);
        }

        return records;
    }

    /**
     * Read a feature record from the passed input stream.
     *
     * @param dis input stream to read from
     * @return the feature record
     * @throws IOException in case the feature record could not be read
     */
    private FeatureRecord readFeatureRecord(DataInputStream dis) throws IOException {
        byte[] featureTagBuffer = new byte[4];
        dis.read(featureTagBuffer);

        String featureTag = new String(featureTagBuffer, StandardCharsets.ISO_8859_1);
        int featureOffset = dis.readUnsignedShort();

        return new FeatureRecord(featureTag, featureOffset);
    }

    /**
     * Read the feature tables from the provided stream.
     *
     * @param dis input stream
     * @throws IOException in case the feature tables could not be read
     */
    private void readFeatureTables(DataInputStream dis) throws IOException {
        FeatureRecord[] records = readFeatureRecords(dis);

        featureTables = new FeatureTable[records.length];
        featureTableTags = new String[records.length];
        for (int i = 0; i < records.length; i++) {
            featureTables[i] = readFeatureTable(records[i], dis);
            featureTableTags[i] = records[i].getTag();
        }
    }

    /**
     * Read a feature table for the given record.
     *
     * @param record to read table for
     * @param dis    input stream
     * @return feature table
     * @throws IOException in case the table could not be read
     */
    private FeatureTable readFeatureTable(FeatureRecord record, DataInputStream dis) throws IOException {
        dis.reset(); // Jump to the beginning of the GPOS table
        dis.skipBytes(header.getFeatureListOffset() + record.getOffset()); // Jump to the beginning of the script table to parse

        int featureParams = dis.readUnsignedShort();

        int lookupIndexCount = dis.readUnsignedShort();
        int[] lookupListIndices = new int[lookupIndexCount];
        for (int i = 0; i < lookupIndexCount; i++) {
            lookupListIndices[i] = dis.readUnsignedShort();
        }

        return new FeatureTable(featureParams, lookupListIndices);
    }

    /**
     * Read the lookup tables.
     *
     * @param dis stream to read from
     * @throws IOException in case the lookup tables could not be read
     */
    private void readLookupTables(DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(header.getLookupListOffset());

        int lookupCount = dis.readUnsignedShort();
        int[] lookupTableOffsets = new int[lookupCount];
        for (int i = 0; i < lookupCount; i++) {
            lookupTableOffsets[i] = dis.readUnsignedShort();
        }

        lookupTables = new LookupTable[lookupTableOffsets.length];
        for (int i = 0; i < lookupTables.length; i++) {
            int offset = lookupTableOffsets[i];

            lookupTables[i] = readLookupTable(offset, dis);
        }
    }

    /**
     * Read the lookup table at the given offset.
     *
     * @param offset of the table
     * @param dis    input stream to read from
     * @return lookup table
     * @throws IOException in case the lookup table could not be read
     */
    private LookupTable readLookupTable(int offset, DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(header.getLookupListOffset() + offset);

        int lookupType = dis.readUnsignedShort();
        int lookupFlag = dis.readUnsignedShort();

        // Parse the lookup flag to the available options
        Set<LookupTableOption> options = LookupTableOption.collect(lookupFlag);

        int subTableCount = dis.readUnsignedShort();
        int[] subTableOffsets = new int[subTableCount];
        for (int i = 0; i < subTableCount; i++) {
            subTableOffsets[i] = dis.readUnsignedShort();
        }

        Integer markFilteringSet = options.contains(LookupTableOption.USE_MARK_FILTERING_SET)
                ? dis.readUnsignedShort()
                : null;

        LookupSubTable[] subTables = new LookupSubTable[subTableCount];
        for (int i = 0; i < subTableCount; i++) {
            subTables[i] = readLookupSubTable(lookupType, offset, subTableOffsets[i], dis);
        }

        return new LookupTable(
                lookupType,
                lookupFlag,
                options,
                subTables,
                markFilteringSet
        );
    }

    /**
     * Read a sub table of a lookup table.
     *
     * @param lookupType        type of the table
     * @param lookupTableOffset offset of the lookup table
     * @param offset            of the sub table from the start of the lookup table
     * @param dis               input stream to read from
     * @return lookup sub table
     * @throws IOException in case the sub table could not be read
     */
    private LookupSubTable readLookupSubTable(int lookupType, int lookupTableOffset, int offset, DataInputStream dis) throws IOException {
        return switch (LookupType.forTypeNumber(lookupType)) {
            case PAIR_ADJUSTMENT_POSITIONING -> readPairAdjustmentSubTable(header.getLookupListOffset() + lookupTableOffset + offset, dis);
            default -> null; // Sub table type not yet implemented
        };
    }

    /**
     * Read the pair adjustment sub table.
     *
     * @param offset of the table in the stream
     * @param dis    input stream to read from
     * @return sub table
     * @throws IOException in case the table could not be read
     */
    private LookupSubTable readPairAdjustmentSubTable(int offset, DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(offset);

        int posFormat = dis.readUnsignedShort();

        if (posFormat == 1) {
            return readPairAdjustmentSubTableFormat1(offset, dis);
        } else if (posFormat == 2) {
            return readPairAdjustmentSubTableFormat2(offset, dis);
        } else {
            throw new IOException(String.format(
                    "Pair adjustment sub table format %d unknown",
                    posFormat
            ));
        }
    }

    /**
     * Read the pair adjustment sub table of format 1.
     *
     * @param offset of the table in the stream
     * @param dis    input stream to read from
     * @return the table
     * @throws IOException in case the table could not be read
     */
    private LookupSubTable readPairAdjustmentSubTableFormat1(int offset, DataInputStream dis) throws IOException {
        int coverageOffset = dis.readUnsignedShort();

        int valueFormat1 = dis.readUnsignedShort();
        int valueFormat2 = dis.readUnsignedShort();

        int pairSetCount = dis.readUnsignedShort();
        int[] pairSetOffsets = new int[pairSetCount];
        for (int i = 0; i < pairSetCount; i++) {
            pairSetOffsets[i] = dis.readUnsignedShort();
        }

        CoverageTable coverageTable = readCoverageTable(offset + coverageOffset, dis);

        PairSetTable[] pairSetTables = new PairSetTable[pairSetOffsets.length];
        for (int i = 0; i < pairSetOffsets.length; i++) {
            pairSetTables[i] = readPairSetTable(offset + pairSetOffsets[i], dis, valueFormat1, valueFormat2);
        }

        return new PairPosFormat1SubTable(
                coverageTable,
                valueFormat1,
                valueFormat2,
                pairSetTables
        );
    }

    /**
     * Read the pair adjustment sub table of format 2.
     *
     * @param offset of the table in the stream
     * @param dis    input stream to read from
     * @return the table
     * @throws IOException in case the table could not be read
     */
    private LookupSubTable readPairAdjustmentSubTableFormat2(int offset, DataInputStream dis) throws IOException {
        int coverageOffset = dis.readUnsignedShort();

        int valueFormat1 = dis.readUnsignedShort();
        int valueFormat2 = dis.readUnsignedShort();

        int classDef1Offset = dis.readUnsignedShort();
        int classDef2Offset = dis.readUnsignedShort();

        int class1Count = dis.readUnsignedShort();
        int class2Count = dis.readUnsignedShort();

        // Gather the value records to read entirely afterwards
        class Class1RecordDataCollector {
            ValueRecordToRead[] valueRecords1ToRead;
            ValueRecordToRead[] valueRecords2ToRead;
        }

        Class1RecordDataCollector[] recordCollectors = new Class1RecordDataCollector[class1Count];
        for (int i = 0; i < class1Count; i++) {
            Class1RecordDataCollector collector = new Class1RecordDataCollector();
            collector.valueRecords1ToRead = new ValueRecordToRead[class2Count];
            collector.valueRecords2ToRead = new ValueRecordToRead[class2Count];

            for (int a = 0; a < class2Count; a++) {
                collector.valueRecords1ToRead[a] = readValueRecordToRead(dis, valueFormat1);
                collector.valueRecords2ToRead[a] = readValueRecordToRead(dis, valueFormat2);
            }

            recordCollectors[i] = collector;
        }

        // Read class 1 records now properly
        Class1Record[] class1Records = new Class1Record[class1Count];
        for (int i = 0; i < class1Count; i++) {
            Class1RecordDataCollector collector = recordCollectors[i];

            Class2Record[] class2Records = new Class2Record[class2Count];
            for (int a = 0; a < class2Count; a++) {
                ValueRecord valueRecord1 = readValueRecord(collector.valueRecords1ToRead[a], offset, dis);
                ValueRecord valueRecord2 = readValueRecord(collector.valueRecords2ToRead[a], offset, dis);

                class2Records[a] = new Class2Record(valueRecord1, valueRecord2);
            }

            class1Records[i] = new Class1Record(class2Records);
        }

        // Read coverage table
        CoverageTable coverageTable = readCoverageTable(offset + coverageOffset, dis);

        // Read class def tables
        ClassDefTable classDef1Table = readClassDefTable(offset + classDef1Offset, dis);
        ClassDefTable classDef2Table = readClassDefTable(offset + classDef2Offset, dis);

        return new PairPosFormat2SubTable(
                coverageTable,
                valueFormat1,
                valueFormat2,
                classDef1Table,
                classDef2Table,
                class1Count,
                class2Count,
                class1Records
        );
    }

    /**
     * Read the class def table at the given offset.
     *
     * @param offset to read class def table at
     * @param dis    input stream to read from
     * @return the class def table
     * @throws IOException in case the class def table could not be read
     */
    private ClassDefTable readClassDefTable(int offset, DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(offset);

        int classFormat = dis.readUnsignedShort();

        if (classFormat == 1) {
            return readClassDefTableFormat1(dis);
        } else if (classFormat == 2) {
            return readClassDefTableFormat2(dis);
        } else {
            throw new IOException(String.format(
                    "Unexpected ClassDefTable format %d",
                    classFormat
            ));
        }
    }

    /**
     * Read the class def table in format 1.
     *
     * @param dis to read table for
     * @return the class def table
     * @throws IOException in case the table could not be read
     */
    private ClassDefTable readClassDefTableFormat1(DataInputStream dis) throws IOException {
        int startGlyphID = dis.readUnsignedShort();

        int glyphCount = dis.readUnsignedShort();
        int[] classValueArray = new int[glyphCount];
        for (int i = 0; i < glyphCount; i++) {
            classValueArray[i] = dis.readUnsignedShort();
        }

        return new ClassDefTableFormat1(startGlyphID, classValueArray);
    }

    /**
     * Read the class def table in format 2.
     *
     * @param dis to read table for
     * @return the class def table
     * @throws IOException in case the table could not be read
     */
    private ClassDefTable readClassDefTableFormat2(DataInputStream dis) throws IOException {
        int classRangeCount = dis.readUnsignedShort();
        ClassRangeRecord[] records = new ClassRangeRecord[classRangeCount];
        for (int i = 0; i < classRangeCount; i++) {
            records[i] = readClassRangeRecord(dis);
        }

        return new ClassDefTableFormat2(records);
    }

    /**
     * Read a class range record from the passed stream.
     *
     * @param dis stream to read from
     * @return the read class range record
     * @throws IOException in case the record could not be read
     */
    private ClassRangeRecord readClassRangeRecord(DataInputStream dis) throws IOException {
        int startGlyphID = dis.readUnsignedShort();
        int endGlyphID = dis.readUnsignedShort();
        int classValue = dis.readUnsignedShort();

        return new ClassRangeRecord(startGlyphID, endGlyphID, classValue);
    }

    /**
     * Read the coverage table at the passed offset from the given stream.
     *
     * @param offset to read table at
     * @param dis    input stream to read from
     * @return the coverage table
     * @throws IOException in case the table could not be read
     */
    private CoverageTable readCoverageTable(int offset, DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(offset);

        int coverageFormat = dis.readUnsignedShort();

        if (coverageFormat == 1) {
            return readCoverageTableFormat1(dis);
        } else if (coverageFormat == 2) {
            return readCoverageTableFormat2(dis);
        } else {
            throw new IOException(String.format(
                    "Could not read coverage table with format %d",
                    coverageFormat
            ));
        }
    }

    /**
     * Read the coverage table in format 1.
     *
     * @param dis stream to read from
     * @return coverage table
     * @throws IOException in case the table could not be read
     */
    private CoverageTable readCoverageTableFormat1(DataInputStream dis) throws IOException {
        int glyphCount = dis.readUnsignedShort();
        int[] glyphIDs = new int[glyphCount];
        for (int i = 0; i < glyphIDs.length; i++) {
            glyphIDs[i] = dis.readUnsignedShort();
        }

        return new CoverageTableFormat1(glyphIDs);
    }

    /**
     * Read the coverage table in format 2.
     *
     * @param dis stream to read from
     * @return coverage table
     * @throws IOException in case the table could not be read
     */
    private CoverageTable readCoverageTableFormat2(DataInputStream dis) throws IOException {
        int rangeCount = dis.readUnsignedShort();
        RangeRecord[] records = new RangeRecord[rangeCount];
        for (int i = 0; i < rangeCount; i++) {
            int startGlyphId = dis.readUnsignedShort();
            int endGlyphId = dis.readUnsignedShort();
            int startCoverageIndex = dis.readUnsignedShort();

            records[i] = new RangeRecord(startGlyphId, endGlyphId, startCoverageIndex);
        }

        return new CoverageTableFormat2(records);
    }

    /**
     * Read the pair set table at the given offset.
     *
     * @param offset       to read table at
     * @param dis          input stream to read from
     * @param valueFormat1 format of the valueRecord1
     * @param valueFormat2 format of the valueRecord2
     * @return the pair set table
     * @throws IOException in case the table could not be read
     */
    private PairSetTable readPairSetTable(int offset, DataInputStream dis, int valueFormat1, int valueFormat2) throws IOException {
        dis.reset();
        dis.skipBytes(offset);

        int pairValueCount = dis.readUnsignedShort();
        int[] secondGlyphs = new int[pairValueCount];
        ValueRecordToRead[] valueRecords1ToRead = new ValueRecordToRead[pairValueCount];
        ValueRecordToRead[] valueRecords2ToRead = new ValueRecordToRead[pairValueCount];
        for (int i = 0; i < pairValueCount; i++) {
            secondGlyphs[i] = dis.readUnsignedShort();
            valueRecords1ToRead[i] = readValueRecordToRead(dis, valueFormat1);
            valueRecords2ToRead[i] = readValueRecordToRead(dis, valueFormat2);
        }

        PairValueRecord[] records = new PairValueRecord[pairValueCount];
        for (int i = 0; i < pairValueCount; i++) {
            records[i] = new PairValueRecord(
                    secondGlyphs[i],
                    readValueRecord(valueRecords1ToRead[i], offset, dis),
                    readValueRecord(valueRecords2ToRead[i], offset, dis)
            );
        }

        return new PairSetTable(records);
    }

    /**
     * Read a value record that is yet to be read properly afterwards.
     *
     * @param dis         input stream
     * @param valueFormat that defines the structure of the value record
     * @return value record to read
     * @throws IOException in case the value record to read could not be read
     */
    private ValueRecordToRead readValueRecordToRead(DataInputStream dis, int valueFormat) throws IOException {
        int xPlacement = (valueFormat & 0x0001) != 0 ? dis.readShort() : 0;
        int yPlacement = (valueFormat & 0x0002) != 0 ? dis.readShort() : 0;

        int xAdvance = (valueFormat & 0x0004) != 0 ? dis.readShort() : 0;
        int yAdvance = (valueFormat & 0x0008) != 0 ? dis.readShort() : 0;

        int xPlaDeviceOffset = (valueFormat & 0x0010) != 0 ? dis.readUnsignedShort() : 0;
        int yPlaDeviceOffset = (valueFormat & 0x0020) != 0 ? dis.readUnsignedShort() : 0;

        int xAdvDeviceOffset = (valueFormat & 0x0040) != 0 ? dis.readUnsignedShort() : 0;
        int yAdvDeviceOffset = (valueFormat & 0x0080) != 0 ? dis.readUnsignedShort() : 0;

        return new ValueRecordToRead(
                xPlacement,
                yPlacement,
                xAdvance,
                yAdvance,
                xPlaDeviceOffset,
                yPlaDeviceOffset,
                xAdvDeviceOffset,
                yAdvDeviceOffset
        );
    }

    /**
     * Read a value record properly.
     *
     * @param toRead the value record details to read with
     * @param offset of the parent table
     * @param dis    input stream to read from
     * @return the read value record
     * @throws IOException in case the record could not be read
     */
    private ValueRecord readValueRecord(ValueRecordToRead toRead, int offset, DataInputStream dis) throws IOException {
        FontUnitAdjustmentTable xPlaDevice = toRead.getXPlaDevice() != 0 ? readFontUnitAdjustmentTable(offset + toRead.getXPlaDevice(), dis) : null;
        FontUnitAdjustmentTable yPlaDevice = toRead.getYPlaDevice() != 0 ? readFontUnitAdjustmentTable(offset + toRead.getYPlaDevice(), dis) : null;
        FontUnitAdjustmentTable xAdvDevice = toRead.getXAdvDevice() != 0 ? readFontUnitAdjustmentTable(offset + toRead.getXAdvDevice(), dis) : null;
        FontUnitAdjustmentTable yAdvDevice = toRead.getYAdvDevice() != 0 ? readFontUnitAdjustmentTable(offset + toRead.getYAdvDevice(), dis) : null;

        return new ValueRecord(
                toRead.getXPlacement(),
                toRead.getYPlacement(),
                toRead.getXAdvance(),
                toRead.getYAdvance(),
                xPlaDevice,
                yPlaDevice,
                xAdvDevice,
                yAdvDevice
        );
    }

    /**
     * Read the font unit adjustment table at the given offset.
     *
     * @param offset of the table
     * @param dis    input stream
     * @return font unit adjustment table
     * @throws IOException in case the table could not be read
     */
    private FontUnitAdjustmentTable readFontUnitAdjustmentTable(int offset, DataInputStream dis) throws IOException {
        dis.reset();
        dis.skipBytes(offset);

        int firstUnsignedShort = dis.readUnsignedShort();
        int secondUnsignedShort = dis.readUnsignedShort();

        int deltaFormatValue = dis.readUnsignedShort();
        DeltaFormat deltaFormat = DeltaFormat.forValue(deltaFormatValue);
        if (deltaFormat == null) {
            throw new IOException(String.format("Could not find delta format for value %d", deltaFormatValue));
        }

        return switch (deltaFormat) {
            case VARIATION_INDEX -> new VariationIndexTable(firstUnsignedShort, secondUnsignedShort, deltaFormat);
            case LOCAL_2_BIT_DELTAS, LOCAL_4_BIT_DELTAS, LOCAL_8_BIT_DELTAS -> {
                // Read delta values first
                int deltaValueCount = secondUnsignedShort - firstUnsignedShort + 1;
                int[] deltaValues = new int[deltaValueCount];

                int count = 0;
                while (count < deltaValueCount) {
                    int src = dis.readUnsignedShort();

                    int[] values = switch (deltaFormat) {
                        case LOCAL_2_BIT_DELTAS -> new int[]{
                                src & 0b1100000000000000,
                                src & 0b0011000000000000,
                                src & 0b0000110000000000,
                                src & 0b0000001100000000,
                                src & 0b0000000011000000,
                                src & 0b0000000000110000,
                                src & 0b0000000000001100,
                                src & 0b0000000000000011
                        };
                        case LOCAL_4_BIT_DELTAS -> new int[]{
                                src & 0b1111000000000000,
                                src & 0b0000111100000000,
                                src & 0b0000000011110000,
                                src & 0b0000000000001111
                        };
                        case LOCAL_8_BIT_DELTAS -> new int[]{
                                src & 0b1111111100000000,
                                src & 0b0000000011111111
                        };
                        default -> throw new IOException();
                    };

                    for (int v : values) {
                        if (count < deltaValueCount) {
                            deltaValues[count++] = v;
                        } else {
                            break;
                        }
                    }
                }

                yield new DeviceTable(
                        firstUnsignedShort,
                        secondUnsignedShort,
                        deltaFormat,
                        deltaValues
                );
            }
            default -> throw new IOException(String.format(
                    "Could not build font adjustment table (Device table or VariationIndex table) for delta format %d",
                    deltaFormatValue
            ));
        };
    }

    /**
     * A ScriptRecord in a GPOS table.
     */
    private static final class ScriptRecord {

        /**
         * Tag of the table.
         */
        private final String tag;

        /**
         * Offset of where to find the actual ScriptTable.
         * Measured from the beginning of the ScriptList.
         */
        private final int offset;

        public ScriptRecord(String tag, int offset) {
            this.tag = tag;
            this.offset = offset;
        }

        public String getTag() {
            return tag;
        }

        public int getOffset() {
            return offset;
        }

    }

    /**
     * A language-system record in a GPOS table.
     * It specifies a tag and offset of the actual language-system table to read.
     */
    private static final class LangSysRecord {

        /**
         * Tag of the table.
         */
        private final String tag;

        /**
         * Offset of where to find the actual language-system table.
         * Measured from the beginning of the ScriptTable.
         */
        private final int offset;

        public LangSysRecord(String tag, int offset) {
            this.tag = tag;
            this.offset = offset;
        }

        public String getTag() {
            return tag;
        }

        public int getOffset() {
            return offset;
        }

    }

    /**
     * A feature record in a GPOS table.
     * It specifies a tag and offset of the actual feature table to read.
     */
    private static final class FeatureRecord {

        /**
         * Tag of the table.
         */
        private final String tag;

        /**
         * Offset of where to find the actual feature table.
         * Measured from the beginning of the FeatureList.
         */
        private final int offset;

        public FeatureRecord(String tag, int offset) {
            this.tag = tag;
            this.offset = offset;
        }

        public String getTag() {
            return tag;
        }

        public int getOffset() {
            return offset;
        }

    }

    /**
     * A value record yet to read properly
     */
    private static class ValueRecordToRead {

        /**
         * Horizontal adjustment for placement (in design units).
         */
        private final int xPlacement;

        /**
         * Vertical adjustment for placement (in design units).
         */
        private final int yPlacement;

        /**
         * Horizontal adjustment for advance (in design units).
         * Only used for horizontal layout.
         */
        private final int xAdvance;

        /**
         * Vertical adjustment for advance (in design units).
         * Only used for vertical layout.
         */
        private final int yAdvance;

        /**
         * The table defining horizontal adjustments to the placement.
         */
        private final int xPlaDevice;

        /**
         * The table defining vertical adjustments to the placement.
         */
        private final int yPlaDevice;

        /**
         * The table defining horizontal adjustments to the advance.
         */
        private final int xAdvDevice;

        /**
         * The table defining vertical adjustments to the advance.
         */
        private final int yAdvDevice;

        public ValueRecordToRead(
                int xPlacement,
                int yPlacement,
                int xAdvance,
                int yAdvance,
                int xPlaDevice,
                int yPlaDevice,
                int xAdvDevice,
                int yAdvDevice
        ) {
            this.xPlacement = xPlacement;
            this.yPlacement = yPlacement;
            this.xAdvance = xAdvance;
            this.yAdvance = yAdvance;
            this.xPlaDevice = xPlaDevice;
            this.yPlaDevice = yPlaDevice;
            this.xAdvDevice = xAdvDevice;
            this.yAdvDevice = yAdvDevice;
        }

        public int getXPlacement() {
            return xPlacement;
        }

        public int getYPlacement() {
            return yPlacement;
        }

        public int getXAdvance() {
            return xAdvance;
        }

        public int getYAdvance() {
            return yAdvance;
        }

        public int getXPlaDevice() {
            return xPlaDevice;
        }

        public int getYPlaDevice() {
            return yPlaDevice;
        }

        public int getXAdvDevice() {
            return xAdvDevice;
        }

        public int getYAdvDevice() {
            return yAdvDevice;
        }

    }

}
