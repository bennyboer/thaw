package de.be.thaw.typeset.knuthplass.paragraph;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.paragraph.floating.Floating;

/**
 * A paragraph.
 */
public interface Paragraph {

    /**
     * Get the floating of the paragraph.
     *
     * @return floating
     */
    Floating getFloating();

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
