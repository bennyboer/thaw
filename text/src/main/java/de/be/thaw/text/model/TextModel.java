package de.be.thaw.text.model;

import de.be.thaw.text.model.box.TextBox;

import java.util.Collection;

/**
 * Text model representing the thaw document text format.
 */
public class TextModel {

    /**
     * Text boxes holding the text contents.
     */
    private final Collection<TextBox> boxes;

    public TextModel(Collection<TextBox> boxes) {
        this.boxes = boxes;
    }

    /**
     * A collection of text boxes the text consists of.
     *
     * @return collection of text boxes
     */
    public Collection<TextBox> boxes() {
        return boxes;
    }

}
