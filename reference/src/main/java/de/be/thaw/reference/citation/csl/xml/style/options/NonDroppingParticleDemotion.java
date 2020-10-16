package de.be.thaw.reference.citation.csl.xml.style.options;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Demotion for non-dropping-particles.
 * See specification at https://docs.citationstyles.org/en/stable/specification.html#name-particles
 */
public enum NonDroppingParticleDemotion {

    NEVER("never"),
    SORT_ONLY("sort-only"),
    DISPLAY_AND_SORT("display-and-sort");

    /**
     * Original value in the CSL specification.
     */
    private final String value;

    NonDroppingParticleDemotion(String value) {
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
