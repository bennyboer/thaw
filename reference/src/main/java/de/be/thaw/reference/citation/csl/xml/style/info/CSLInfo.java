package de.be.thaw.reference.citation.csl.xml.style.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.be.thaw.reference.citation.csl.xml.style.info.link.CSLLink;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Info element of the CSL style specification.
 */
public class CSLInfo {

    /**
     * ID (URI) of the style.
     */
    private String id;

    /**
     * List of authors of the style.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("author")
    private List<CSLAuthor> authors = new ArrayList<>();

    /**
     * List of contributors to the style.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("contributor")
    private List<CSLAuthor> contributors = new ArrayList<>();

    /**
     * List of categories that apply to this style.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("category")
    private List<CSLCategory> categories = new ArrayList<>();

    /**
     * ISSN identifiers.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> issn = new ArrayList<>();

    /**
     * eISSN identifiers.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> eissn = new ArrayList<>();

    /**
     * ISSN-L identifiers.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> issnl = new ArrayList<>();

    /**
     * Links of the style.
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("link")
    private List<CSLLink> links = new ArrayList<>();

    /**
     * Timestamp of when the style was initially created or made available.
     */
    @Nullable
    private String published;

    /**
     * Rights element that specifies the license under which the style file is released.
     */
    @Nullable
    private CSLRights rights;

    /**
     * Summary of the style.
     */
    @Nullable
    private String summary;

    /**
     * Title of the style.
     */
    private String title;

    /**
     * A shortened title
     */
    @JacksonXmlProperty(localName = "title-short")
    @Nullable
    private String shortTitle;

    /**
     * Timestamp of when the style was last updated.
     */
    private String updated;

    public List<CSLAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(List<CSLAuthor> authors) {
        this.authors = authors;
    }

    public List<CSLAuthor> getContributors() {
        return contributors;
    }

    public void setContributors(List<CSLAuthor> contributors) {
        this.contributors = contributors;
    }

    public List<CSLCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<CSLCategory> categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getIssn() {
        return issn;
    }

    public void setIssn(List<String> issn) {
        this.issn = issn;
    }

    public List<String> getEissn() {
        return eissn;
    }

    public void setEissn(List<String> eissn) {
        this.eissn = eissn;
    }

    public List<String> getIssnl() {
        return issnl;
    }

    public void setIssnl(List<String> issnl) {
        this.issnl = issnl;
    }

    public List<CSLLink> getLinks() {
        return links;
    }

    public void setLinks(List<CSLLink> links) {
        this.links = links;
    }

    @Nullable
    public String getPublished() {
        return published;
    }

    public void setPublished(@Nullable String published) {
        this.published = published;
    }

    @Nullable
    public CSLRights getRights() {
        return rights;
    }

    public void setRights(@Nullable CSLRights rights) {
        this.rights = rights;
    }

    @Nullable
    public String getSummary() {
        return summary;
    }

    public void setSummary(@Nullable String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(@Nullable String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

}
