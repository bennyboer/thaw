package de.be.thaw.typeset.page;

import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

/**
 * An element of a de.be.thaw.typeset.page.
 */
public interface Element {

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

}
