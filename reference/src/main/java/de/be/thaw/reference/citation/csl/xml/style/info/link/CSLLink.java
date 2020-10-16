package de.be.thaw.reference.citation.csl.xml.style.info.link;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import org.jetbrains.annotations.Nullable;

/**
 * Link element in a CSL info.
 */
public class CSLLink {

    /**
     * Hyper reference.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String href;

    /**
     * Link relation to the style.
     */
    @JacksonXmlProperty(isAttribute = true)
    private CSLLinkRel rel;

    /**
     * The link element may contain a description as text.
     */
    @JacksonXmlText
    @Nullable
    private String description;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public CSLLinkRel getRel() {
        return rel;
    }

    public void setRel(CSLLinkRel rel) {
        this.rel = rel;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

}
