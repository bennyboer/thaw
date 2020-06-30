package de.be.thaw.core.typesetting.page.element;

import de.be.thaw.core.util.Position;
import de.be.thaw.core.util.Size;

/**
 * An element of a page.
 */
public interface Element {

    /**
     * Get the size of the element.
     *
     * @return size
     */
    Size getSize();

    /**
     * Get the position of the element on a page.
     *
     * @return position
     */
    Position getPosition();

}
