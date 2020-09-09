package de.be.thaw.core.document.builder.impl.source;

import de.be.thaw.core.document.Document;
import de.be.thaw.info.ThawInfo;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.reference.citation.source.model.SourceModel;
import de.be.thaw.reference.impl.DefaultReferenceModel;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.text.model.TextModel;
import org.jetbrains.annotations.Nullable;

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

    /**
     * The source model.
     */
    private final SourceModel sourceModel;

    /**
     * Reference model to use during building.
     */
    private final ReferenceModel referenceModel;

    /**
     * Parent document that is specified when typesetting nested.
     */
    @Nullable
    private final Document parentDocument;

    public DocumentBuildSource(ThawInfo info, TextModel textModel, StyleModel styleModel, SourceModel sourceModel) {
        this(info, textModel, styleModel, sourceModel, new DefaultReferenceModel(), null);
    }

    public DocumentBuildSource(ThawInfo info, TextModel textModel, StyleModel styleModel, SourceModel sourceModel, ReferenceModel referenceModel, @Nullable Document parentDocument) {
        this.info = info;
        this.textModel = textModel;
        this.styleModel = styleModel;
        this.sourceModel = sourceModel;
        this.referenceModel = referenceModel;
        this.parentDocument = parentDocument;
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

    public SourceModel getSourceModel() {
        return sourceModel;
    }

    public ReferenceModel getReferenceModel() {
        return referenceModel;
    }

    @Nullable
    public Document getParentDocument() {
        return parentDocument;
    }

}
