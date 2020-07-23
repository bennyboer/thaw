package de.be.thaw.math.mathml.typeset.element;

import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A typeset math element.
 */
public interface MathElement {

    /**
     * Get the size of the element.
     *
     * @return size
     */
    Size getSize();

    /**
     * Get the position of the element.
     *
     * @return position
     */
    Position getPosition();

}
