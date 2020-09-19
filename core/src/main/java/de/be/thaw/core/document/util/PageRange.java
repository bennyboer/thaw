package de.be.thaw.core.document.util;

/**
 * Range of pages.
 */
public class PageRange {

    /**
     * Identifier for the last page.
     */
    public static final int LAST_PAGE = -1;

    /**
     * The start page of the range.
     */
    private int startPage = 1;

    /**
     * The end page of the range.
     * If this is -1 it means that it is the last page.
     */
    private int endPage = LAST_PAGE;

    public PageRange(int startPage, int endPage) {
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

}
