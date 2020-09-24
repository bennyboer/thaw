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

    /**
     * The numbering string to display.
     */
    private final String numberingString;

    public TableOfContentsItemParagraph(double lineWidth, DocumentNode node, double pageNumberWidth, String numberingString) {
        super(lineWidth, node);

        this.pageNumberWidth = pageNumberWidth;
        this.numberingString = numberingString;
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.TOC_ITEM;
    }

    /**
     * Get the maximum page number width.
     *
     * @return page number width
     */
    public double getPageNumberWidth() {
        return pageNumberWidth;
    }

    /**
     * Get the numbering string to display.
     *
     * @return numbering string
     */
    public String getNumberingString() {
        return numberingString;
    }

}
