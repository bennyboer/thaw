package de.be.thaw.reference.citation.csl.xml.style;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.be.thaw.reference.citation.csl.xml.style.info.CSLInfo;
import de.be.thaw.reference.citation.csl.xml.style.options.GlobalOptions;
import de.be.thaw.reference.citation.csl.xml.style.options.StyleClass;
import org.jetbrains.annotations.Nullable;

/**
 * Style of the citation style language (CSL).
 * Parsed from XML files.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CSLStyle extends GlobalOptions {

    /**
     * Determines whether the style uses in-text citations (value "in-text") or notes ("note").
     */
    @JacksonXmlProperty(isAttribute = true, localName = "class")
    private StyleClass styleClass;

    /**
     * Style version.
     */
    @JacksonXmlProperty(isAttribute = true)
    private String version;

    /**
     * Optional default locale to use.
     * Must be a locale code.
     */
    @JacksonXmlProperty(isAttribute = true, localName = "default-locale")
    @Nullable
    private String defaultLocale;

    /**
     * Info of the style.
     */
    private CSLInfo info;

    public StyleClass getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(StyleClass styleClass) {
        this.styleClass = styleClass;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Nullable
    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(@Nullable String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public CSLInfo getInfo() {
        return info;
    }

    public void setInfo(CSLInfo info) {
        this.info = info;
    }

}
