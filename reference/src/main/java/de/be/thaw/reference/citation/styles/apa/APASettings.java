package de.be.thaw.reference.citation.styles.apa;

import java.text.DateFormat;

/**
 * Settings for the APA citation style.
 */
public class APASettings {

    /**
     * String shown when a publication date of a source is unknown.
     */
    private String noDateStr = "n. d.";

    /**
     * The starting quotation mark of a quote.
     */
    private String startQuotationMark = "\"";

    /**
     * The ending quotation mark of a quote.
     */
    private String endQuotationMark = "\"";

    /**
     * The word "and" written out.
     */
    private String andStr = "and";

    /**
     * The date format to use.
     */
    private DateFormat dateFormat = DateFormat.getDateInstance();

    /**
     * Get the string shown when a publication date of a source is unknown.
     *
     * @return no date string
     */
    public String getNoDateStr() {
        return noDateStr;
    }

    /**
     * Set the string shown when a publication date of a source is unknown.
     *
     * @param noDateStr to set
     */
    public void setNoDateStr(String noDateStr) {
        this.noDateStr = noDateStr;
    }

    public String getStartQuotationMark() {
        return startQuotationMark;
    }

    public void setStartQuotationMark(String startQuotationMark) {
        this.startQuotationMark = startQuotationMark;
    }

    public String getEndQuotationMark() {
        return endQuotationMark;
    }

    public void setEndQuotationMark(String endQuotationMark) {
        this.endQuotationMark = endQuotationMark;
    }

    public String getAndStr() {
        return andStr;
    }

    public void setAndStr(String andStr) {
        this.andStr = andStr;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

}
