package de.be.thaw.core.document.builder.impl.source;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.text.model.TextModel;

/**
 * Source using the text, style and info model to build a document.
 */
public class DocumentBuildSource {

    /**
     * The info model.
     */
    private final ThawInfo info;

    /**
     * The text model.
     */
    private final TextModel textModel;

    /**
     * The style model.
     */
    private final StyleModel styleModel;

    public DocumentBuildSource(ThawInfo info, TextModel textModel, StyleModel styleModel) {
        this.info = info;
        this.textModel = textModel;
        this.styleModel = styleModel;
    }

    public ThawInfo getInfo() {
        return info;
    }

    public TextModel getTextModel() {
        return textModel;
    }

    public StyleModel getStyleModel() {
        return styleModel;
    }

}
