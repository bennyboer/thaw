package de.be.thaw.typeset.knuthplass.paragraph;

import de.be.thaw.core.document.node.DocumentNode;

/**
 * A paragraph.
 */
public interface Paragraph {

    /**
     * Get the type of the paragraph.
     *
     * @return paragraph type
     */
    ParagraphType getType();

    /**
     * Get the floating of the paragraph.
     *
     * @return floating
     */
    boolean isFloating();

    /**
     * Get the line width for the passed line number.
     *
     * @param lineNumber to get width for
     * @return line width
     */
    double getLineWidth(int lineNumber);

    /**
     * Get the node representing the paragraph.
     *
     * @return node
     */
    DocumentNode getNode();

}
