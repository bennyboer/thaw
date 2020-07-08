package de.be.thaw.typeset.knuthplass.paragraph.impl.image;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.floating.Floating;

/**
 * Paragraph representing an image.
 */
public class ImageParagraph extends AbstractParagraph {

    /**
     * The image source.
     */
    private final ImageSource src;

    /**
     * Floating of the paragraph.
     */
    private final Floating floating;

    public ImageParagraph(double lineWidth, DocumentNode node, ImageSource src, Floating floating) {
        super(lineWidth, node);

        this.src = src;
        this.floating = floating;
    }

    public ImageSource getSrc() {
        return src;
    }

    @Override
    public Floating getFloating() {
        return floating;
    }

}
