package de.be.thaw.text.model.element.impl;

import de.be.thaw.text.model.element.TextElement;
import de.be.thaw.text.model.element.TextElementType;

/**
 * Text element containing code.
 */
public class CodeTextElement implements TextElement {

    /**
     * Text of the element.
     */
    private final String text;

    public CodeTextElement(String text) {
        this.text = text;
    }

    @Override
    public TextElementType getType() {
        return TextElementType.CODE;
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
