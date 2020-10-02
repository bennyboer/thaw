package de.be.thaw.font.opentype.gpos;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Script table of a glyph positioning (GPOS) table.
 */
final class ScriptTable {

    /**
     * Offset to the default LangSys table from the beginning of the script table.
     */
    @Nullable
    private final LangSysTable defaultLangSysTable;

    /**
     * Available LangSys tables (without the default table).
     */
    private final Map<String, LangSysTable> tableLookup;

    public ScriptTable(@Nullable LangSysTable defaultLangSysTable, Map<String, LangSysTable> tableLookup) {
        this.defaultLangSysTable = defaultLangSysTable;
        this.tableLookup = tableLookup;
    }

    /**
     * Get the default LangSys table.
     *
     * @return default LangSys table
     */
    public Optional<LangSysTable> getDefaultLangSysTable() {
        return Optional.ofNullable(defaultLangSysTable);
    }

    /**
     * Get a LangSys table by its tag.
     *
     * @param tag of the table
     * @return the table of an empty Optional
     */
    public Optional<LangSysTable> getTable(String tag) {
        return Optional.ofNullable(tableLookup.get(tag));
    }

    /**
     * Get a lookup of all LangSys tables.
     *
     * @return table lookup
     */
    public Map<String, LangSysTable> getTableLookup() {
        return tableLookup;
    }

}
