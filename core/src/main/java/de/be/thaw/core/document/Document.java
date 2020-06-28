package de.be.thaw.core.document;

import de.be.thaw.text.model.TextModel;

/**
 * Representation of the thaw document.
 */
public class Document {

    /**
     * Text model defining the structure and content of the document.
     * For example headings, paragraphs, images, ...
     */
    private final TextModel textModel;

    public Document(TextModel textModel) {
        this.textModel = textModel;
    }

    public TextModel getTextModel() {
        return textModel;
    }

}
