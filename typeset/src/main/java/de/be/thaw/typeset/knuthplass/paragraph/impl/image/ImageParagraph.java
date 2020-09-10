package de.be.thaw.typeset.knuthplass.paragraph.impl.image;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.util.HorizontalAlignment;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    /**
     * Caption of the image.
     */
    @Nullable
    private final String caption;

    /**
     * Prefix of the caption.
     * Should be something like "Figure" but is customizable.
     */
    @Nullable
    private final String captionPrefix;

    public ImageParagraph(
            double lineWidth,
            DocumentNode node,
            ImageSource src,
            boolean floating,
            HorizontalAlignment alignment,
            @Nullable String caption,
            @Nullable String captionPrefix
    ) {
        super(lineWidth, node);

        this.src = src;
        this.floating = floating;
        this.alignment = alignment;
        this.caption = caption;
        this.captionPrefix = captionPrefix;
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

    /**
     * Get the image paragraphs alignment.
     *
     * @return alignment
     */
    public HorizontalAlignment getAlignment() {
        return alignment;
    }

    /**
     * Get the caption of the image paragraph.
     *
     * @return caption
     */
    public Optional<String> getCaption() {
        return Optional.ofNullable(caption);
    }

    /**
     * Prefix of the caption.
     * Should be something like "Figure" but is customizable.
     */
    @Nullable
    public String getCaptionPrefix() {
        return captionPrefix;
    }

}
