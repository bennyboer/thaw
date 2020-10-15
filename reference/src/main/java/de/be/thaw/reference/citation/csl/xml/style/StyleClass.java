package de.be.thaw.reference.citation.csl.xml.style;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Style class for in-text citations.
 */
public enum StyleClass {

    /**
     * Style allows in-text citations.
     */
    IN_TEXT("in-text"),

    /**
     * Style allows only note citations.
     */
    NOTE("note");

    /**
     * Value used in the CSL specifications.
     */
    private final String value;

    StyleClass(String value) {
        this.value = value;
    }

    /**
     * Get the value used in the CSL specification.
     *
     * @return value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

}
