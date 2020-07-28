package de.be.thaw.typeset.knuthplass.paragraph.impl.image;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;

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
    private final HorizontalAlignment alignment;

    public ImageParagraph(double lineWidth, DocumentNode node, ImageSource src, boolean floating, HorizontalAlignment alignment) {
        super(lineWidth, node);

        this.src = src;
        this.floating = floating;
        this.alignment = alignment;
    }

    public ImageSource getSrc() {
        return src;
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.IMAGE;
    }

    @Override
    public boolean isFloating() {
        return floating;
    }

    public HorizontalAlignment getAlignment() {
        return alignment;
    }

}
