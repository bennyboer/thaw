package de.be.thaw.core.document;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.text.model.TextModel;

/**
 * Representation of the thaw document.
 */
public class Document {

    /**
     * Info about the document.
     */
    private final ThawInfo info;

    /**
     * Text model defining the structure and content of the document.
     * For example headings, paragraphs, images, ...
     */
    private final TextModel textModel;

    public Document(ThawInfo info, TextModel textModel) {
        this.info = info;
        this.textModel = textModel;
    }

    /**
     * Get info about the document.
     *
     * @return info
     */
    public ThawInfo getInfo() {
        return info;
    }

    /**
     * Get the text model.
     *
     * @return text model
     */
    public TextModel getTextModel() {
        return textModel;
    }

}
