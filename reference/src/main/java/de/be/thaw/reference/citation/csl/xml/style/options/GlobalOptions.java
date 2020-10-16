package de.be.thaw.reference.citation.csl.xml.style.options;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.jetbrains.annotations.Nullable;

/**
 * Global options allowed in a CSL specification.
 */
public abstract class GlobalOptions extends InheritableNameOptions {

    /**
     * Specifies whether compound given names (e.g. "Jean-Luc") should be initialized
     * with a hyphen ("J.-L.", value "true" = default) or without ("J.L.", value "false").
     */
    @JacksonXmlProperty(isAttribute = true, localName = "initialize-with-hyphen")
    private boolean initializeWithHyphen = true;

    /**
     * Page range format to use.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "page-range-format")
    private PageRangeFormat pageRangeFormat = PageRangeFormat.UNFORMATTED;

    /**
     * Delimiter for page ranges (e. g. 332-345, where '-' is the delimiter).
     */
    @JacksonXmlProperty(isAttribute = true, localName = "page-range-delimiter")
    private String pageRangeDelimiter = "-";

    /**
     * Demotion type for non-dropping particles.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "demote-non-dropping-particle")
    private NonDroppingParticleDemotion demoteNonDroppingParticle = NonDroppingParticleDemotion.DISPLAY_AND_SORT;

    public boolean isInitializeWithHyphen() {
        return initializeWithHyphen;
    }

    public void setInitializeWithHyphen(boolean initializeWithHyphen) {
        this.initializeWithHyphen = initializeWithHyphen;
    }

    @Nullable
    public PageRangeFormat getPageRangeFormat() {
        return pageRangeFormat;
    }

    public void setPageRangeFormat(@Nullable PageRangeFormat pageRangeFormat) {
        this.pageRangeFormat = pageRangeFormat;
    }

    public String getPageRangeDelimiter() {
        return pageRangeDelimiter;
    }

    public void setPageRangeDelimiter(String pageRangeDelimiter) {
        this.pageRangeDelimiter = pageRangeDelimiter;
    }

    public NonDroppingParticleDemotion getDemoteNonDroppingParticle() {
        return demoteNonDroppingParticle;
    }

    public void setDemoteNonDroppingParticle(NonDroppingParticleDemotion demoteNonDroppingParticle) {
        this.demoteNonDroppingParticle = demoteNonDroppingParticle;
    }

}
