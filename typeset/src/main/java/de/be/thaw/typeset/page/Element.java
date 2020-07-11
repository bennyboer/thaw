package de.be.thaw.typeset.page;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

import java.util.Optional;

/**
 * An element of a de.be.thaw.typeset.page.
 */
public interface Element {

    /**
     * Get the number of the page the element is on.
     *
     * @return page number
     */
    int getPageNumber();

    /**
     * Get the size of the element.
     *
     * @return size
     */
    Size getSize();

    /**
     * Get the position of the element on a de.be.thaw.typeset.page.
     *
     * @return position
     */
    Position getPosition();

    /**
     * Get the element type.
     *
     * @return type
     */
    ElementType getType();

    /**
     * Get the original node this element belongs to (if any).
     *
     * @return original node
     */
    Optional<DocumentNode> getNode();

}
