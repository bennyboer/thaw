package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

import java.util.List;

/**
 * Style setting of the header and footer of the document.
 */
public class HeaderFooterStyle implements Style {

    /**
     * Folder to include as header.
     */
    private final HeaderFooterSettings header;

    /**
     * Folder to include as footer.
     */
    private final HeaderFooterSettings footer;

    public HeaderFooterStyle(HeaderFooterSettings header, HeaderFooterSettings footer) {
        this.header = header;
        this.footer = footer;
    }

    @Override
    public StyleType getType() {
        return StyleType.HEADER_FOOTER;
    }

    @Override
    public Style merge(Style style) {
        return this;
    }

    public HeaderFooterSettings getHeader() {
        return header;
    }

    public HeaderFooterSettings getFooter() {
        return footer;
    }

    public static class HeaderFooterSettings {

        /**
         * The default source folder of the header or footer.
         */
        private final String defaultSrc;

        /**
         * List of special headers or footers.
         */
        private final List<SpecialHeaderFooterSettings> specialSettings;

        public HeaderFooterSettings(String defaultSrc, List<SpecialHeaderFooterSettings> specialSettings) {
            this.defaultSrc = defaultSrc;
            this.specialSettings = specialSettings;
        }

        public String getDefaultSrc() {
            return defaultSrc;
        }

        public List<SpecialHeaderFooterSettings> getSpecialSettings() {
            return specialSettings;
        }

    }

    public static class SpecialHeaderFooterSettings {

        /**
         * The start page of the header or footer.
         */
        private final Integer startPage;

        /**
         * The end page of the header or footer.
         */
        private final Integer endPage;

        /**
         * The source folder to use.
         */
        private final String src;

        public SpecialHeaderFooterSettings(Integer startPage, Integer endPage, String src) {
            this.startPage = startPage;
            this.endPage = endPage;
            this.src = src;
        }

        public Integer getStartPage() {
            return startPage;
        }

        public Integer getEndPage() {
            return endPage;
        }

        public String getSrc() {
            return src;
        }

    }

}
