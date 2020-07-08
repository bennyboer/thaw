package de.be.thaw.typeset.knuthplass.paragraph.impl.image;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.style.model.style.text.TextAlignment;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;

/**
 * Paragraph representing an image.
 */
public class ImageParagraph extends AbstractParagraph {

    /**
     * The image source.
     */
    private final ImageSource src;

    /**
     * Whether the paragraph is floating.
     */
    private final boolean floating;

    /**
     * Alignment of the paragraph.
     */
    private final TextAlignment alignment;

    public ImageParagraph(double lineWidth, DocumentNode node, ImageSource src, boolean floating, TextAlignment alignment) {
        super(lineWidth, node);

        this.src = src;
        this.floating = floating;
        this.alignment = alignment;
    }

    public ImageSource getSrc() {
        return src;
    }

    @Override
    public boolean isFloating() {
        return floating;
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

}
