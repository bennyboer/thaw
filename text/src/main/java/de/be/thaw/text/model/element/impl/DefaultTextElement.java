package de.be.thaw.text.model.element.impl;

import de.be.thaw.text.model.element.TextElement;
import de.be.thaw.text.model.element.TextElementType;
import de.be.thaw.text.model.element.emphasis.TextEmphasis;

import java.util.Set;

/**
 * Text element that contains text with or without formatting as bold, italic, underlined or code.
 * The elements containing text must be completely formatted using the set options.
 * For example the contained text "hello world":
 * "hello" cannot be formatted differently than "world" -> Should be two different text elements
 */
public class DefaultTextElement implements TextElement {

    /**
     * Text of the element.
     */
    private final String text;

    /**
     * Emphases set on the whole text in this element.
     */
    private final Set<TextEmphasis> emphases;

    public DefaultTextElement(Set<TextEmphasis> emphases, String text) {
        this.emphases = emphases;
        this.text = text;
    }

    @Override
    public TextElementType getType() {
        return TextElementType.DEFAULT;
    }

    /**
     * Get special emphases set on the whole text in the element.
     *
     * @return emphases that apply on the text
     */
    public Set<TextEmphasis> emphases() {
        return emphases;
    }

    /**
     * Get the text in the element.
     *
     * @return elements text
     */
    public String getText() {
        return text;
    }

}
