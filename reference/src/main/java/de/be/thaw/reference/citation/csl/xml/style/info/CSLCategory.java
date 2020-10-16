package de.be.thaw.reference.citation.csl.xml.style.info;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.jetbrains.annotations.Nullable;

/**
 * Category that applies to a style.
 */
public class CSLCategory {

    /**
     * Citation format of the CSL style.
     * Should only be specified once in a category element for each CSL style!
     */
    @JacksonXmlProperty(isAttribute = true, localName = "citation-format")
    @Nullable
    private CitationFormat citationFormat;

    /**
     * Discipline field of the CSL style.
     */
    @Nullable
    private String field;

    @Nullable
    public CitationFormat getCitationFormat() {
        return citationFormat;
    }

    public void setCitationFormat(@Nullable CitationFormat citationFormat) {
        this.citationFormat = citationFormat;
    }

    @Nullable
    public String getField() {
        return field;
    }

    public void setField(@Nullable String field) {
        this.field = field;
    }

}
