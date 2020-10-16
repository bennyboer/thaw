package de.be.thaw.reference.citation.csl.xml.style.info;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Available citation format for CSL styles.
 * They describe how CSL styles define in-text citations.
 */
public enum CitationFormat {

    /**
     * Author date citation format: e. g. (Doe, 1999).
     */
    AUTHOR_DATE("author-date"),

    /**
     * Only the author: e. g. (Doe).
     */
    AUTHOR("author"),

    /**
     * Only a number: e. g. [1].
     */
    NUMERIC("numeric"),

    /**
     * A label as in-text-citation: e. g. [doe99].
     */
    LABEL("label"),

    /**
     * In-text-citation that appears as a foot- or endnote.
     */
    NOTE("note");

    /**
     * Original value of the enum item in the CSL specification.
     */
    private final String value;

    CitationFormat(String value) {
        this.value = value;
    }

    /**
     * Get the original value of the enum item in the CSL specification.
     *
     * @return value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

}
