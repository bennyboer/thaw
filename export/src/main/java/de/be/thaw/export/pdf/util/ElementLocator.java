package de.be.thaw.export.pdf.util;

import de.be.thaw.typeset.page.Element;

/**
 * Locator of an element in a typeset document (multiple pages).
 */
public class ElementLocator {

    /**
     * The page number the element is on.
     */
    private final int pageNumber;

    /**
     * The actual element.
     */
    private final Element element;

    public ElementLocator(int pageNumber, Element element) {
        this.pageNumber = pageNumber;
        this.element = element;
    }

    /**
     * Get the page number the element is on.
     *
     * @return page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Get the actual element.
     *
     * @return element
     */
    public Element getElement() {
        return element;
    }

}
