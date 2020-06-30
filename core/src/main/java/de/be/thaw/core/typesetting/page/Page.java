package de.be.thaw.core.typesetting.page;

import de.be.thaw.core.typesetting.page.element.Element;

import java.util.List;

/**
 * A ready to print page representation.
 */
public class Page {

    /**
     * The page number.
     */
    private final int number;

    /**
     * Elements on the page.
     */
    private final List<Element> elements;

    public Page(int number, List<Element> elements) {
        this.number = number;
        this.elements = elements;
    }

    /**
     * Get the page number.
     *
     * @return page number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the elements on the page.
     *
     * @return elements
     */
    public List<Element> getElements() {
        return elements;
    }

}
