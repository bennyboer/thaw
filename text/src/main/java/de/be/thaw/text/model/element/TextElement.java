package de.be.thaw.text.model.element;

/**
 * A text element in the thaw document text emphasis.
 * This is an inline element of a text box.
 * For example sentences, thingys, ...
 */
public interface TextElement {

    /**
     * Get the type of the element.
     *
     * @return element type
     */
    TextElementType getType();

}
