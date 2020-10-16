package de.be.thaw.reference.citation.csl.xml.style.info.link;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Link relation to a style.
 */
public enum CSLLinkRel {

    /**
     * Link to the style itself.
     */
    SELF("self"),

    /**
     * Link to the style from which the current style is derived.
     */
    TEMPLATE("template"),

    /**
     * Link to the style documentation.
     */
    DOCUMENTATION("documentation"),

    /**
     * Link to the independent parent style if this style is a dependent style.
     */
    INDEPENDENT_PARENT("independent-parent");

    /**
     * Original value of the enum item in the CSL specification.
     */
    private final String value;

    CSLLinkRel(String value) {
        this.value = value;
    }

    /**
     * Get the original value in the CSL specification.
     *
     * @return value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

}
