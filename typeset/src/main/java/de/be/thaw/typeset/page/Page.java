package de.be.thaw.typeset.page;

import java.util.List;

/**
 * A ready to print de.be.thaw.typeset.page representation.
 */
public class Page {

    /**
     * The de.be.thaw.typeset.page number.
     */
    private final int number;

    /**
     * Elements on the de.be.thaw.typeset.page.
     */
    private final List<Element> elements;

    public Page(int number, List<Element> elements) {
        this.number = number;
        this.elements = elements;
    }

    /**
     * Get the de.be.thaw.typeset.page number.
     *
     * @return de.be.thaw.typeset.page number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Get the elements on the de.be.thaw.typeset.page.
     *
     * @return elements
     */
    public List<Element> getElements() {
        return elements;
    }

}
