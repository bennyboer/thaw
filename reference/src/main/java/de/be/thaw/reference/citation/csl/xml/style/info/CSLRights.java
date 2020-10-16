package de.be.thaw.reference.citation.csl.xml.style.info;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * Rights of a CSL style in the info element.
 */
public class CSLRights {

    /**
     * License URI.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String license;

    /**
     * Description of the rights.
     */
    @JacksonXmlText
    private String description;

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
