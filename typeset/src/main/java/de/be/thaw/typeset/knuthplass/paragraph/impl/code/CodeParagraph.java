package de.be.thaw.typeset.knuthplass.paragraph.impl.code;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Paragraph representing a code block.
 */
public class CodeParagraph extends AbstractParagraph {

    /**
     * The code formatted in RTF-format.
     */
    private final String rtfCode;

    /**
     * The start line to display from.
     */
    private final int startLine;

    /**
     * The end line to display to.
     */
    private final int endLine;

    /**
     * Caption of the code paragraph.
     */
    @Nullable
    private final String caption;

    /**
     * Prefix of the caption.
     * Should be something like "Listing" but is customizable.
     */
    @Nullable
    private final String captionPrefix;

    public CodeParagraph(
            String rtfCode,
            int startLine,
            int endLine,
            double lineWidth,
            DocumentNode node,
            @Nullable String caption,
            @Nullable String captionPrefix
    ) {
        super(lineWidth, node);

        this.rtfCode = rtfCode;
        this.startLine = startLine;
        this.endLine = endLine;

        this.caption = caption;
        this.captionPrefix = captionPrefix;
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.CODE;
    }

    /**
     * Get the RTF-formatted code.
     *
     * @return RTF-formatted code
     */
    public String getRtfCode() {
        return rtfCode;
    }

    /**
     * Get the start line to display from.
     *
     * @return start line
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Get the end line to display to.
     *
     * @return end line
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Get the caption of the code paragraph.
     *
     * @return caption
     */
    public Optional<String> getCaption() {
        return Optional.ofNullable(caption);
    }

    /**
     * Prefix of the caption.
     * Should be something like "Listing" but is customizable.
     */
    @Nullable
    public String getCaptionPrefix() {
        return captionPrefix;
    }

}
