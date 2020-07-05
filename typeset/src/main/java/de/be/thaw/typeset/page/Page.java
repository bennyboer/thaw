package de.be.thaw.typeset.page;

import de.be.thaw.typeset.util.Insets;
import de.be.thaw.typeset.util.Size;

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
     * Size of the page.
     */
    private final Size size;

    /**
     * Insets of the page.
     */
    private final Insets insets;

    /**
     * Elements on the page.
     */
    private final List<Element> elements;

    public Page(int number, Size size, Insets insets, List<Element> elements) {
        this.number = number;
        this.size = size;
        this.insets = insets;
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
     * Get the size of the page.
     *
     * @return size
     */
    public Size getSize() {
        return size;
    }

    /**
     * Get the pages insets.
     *
     * @return insets
     */
    public Insets getInsets() {
        return insets;
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
