package de.be.thaw.text.model.box.impl;

import de.be.thaw.text.model.box.TextBox;
import de.be.thaw.text.model.element.TextElement;

import java.util.Collection;

/**
 * The default text box implementation.
 */
public class DefaultTextBox implements TextBox {

    /**
     * A collection of containing text elements.
     */
    private final Collection<TextElement> elements;

    public DefaultTextBox(Collection<TextElement> elements) {
        this.elements = elements;
    }

    @Override
    public Collection<TextElement> elements() {
        return elements;
    }

}
