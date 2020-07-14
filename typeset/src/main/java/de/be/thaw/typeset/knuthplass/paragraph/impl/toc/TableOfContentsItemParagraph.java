package de.be.thaw.typeset.knuthplass.paragraph.impl.toc;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

/**
 * Paragraph representing an item in the table of contents.
 */
public class TableOfContentsItemParagraph extends TextParagraph {

    /**
     * Available width for the page number.
     */
    private final double pageNumberWidth;

    public TableOfContentsItemParagraph(double lineWidth, DocumentNode node, double pageNumberWidth) {
        super(lineWidth, node);

        this.pageNumberWidth = pageNumberWidth;
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.TOC_ITEM;
    }

    public double getPageNumberWidth() {
        return pageNumberWidth;
    }

}
