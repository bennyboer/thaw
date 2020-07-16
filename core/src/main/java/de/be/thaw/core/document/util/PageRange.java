package de.be.thaw.core.document.util;

import java.util.Optional;

/**
 * Range of pages.
 */
public class PageRange {

    /**
     * The start page of the range.
     */
    private final Integer startPage;

    /**
     * The end page of the range.
     */
    private final Integer endPage;

    public PageRange(Integer startPage, Integer endPage) {
        this.startPage = startPage;
        this.endPage = endPage;
    }

    public Optional<Integer> getStartPage() {
        return Optional.ofNullable(startPage);
    }

    public Optional<Integer> getEndPage() {
        return Optional.ofNullable(endPage);
    }

}
