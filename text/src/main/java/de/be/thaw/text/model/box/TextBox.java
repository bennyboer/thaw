package de.be.thaw.text.model.box;

import de.be.thaw.text.model.element.TextElement;

import java.util.Collection;

/**
 * A text box is a section in the thaw document text emphasis.
 * This might be a text paragraph, heading, image, table, ...
 * So essentially everything that has a virtual size and belongs together.
 */
public interface TextBox {

    /**
     * Get a list of text elements in the box.
     *
     * @return text elements
     */
    Collection<TextElement> elements();

}
