package de.be.thaw.typeset.page;

import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

/**
 * An abstract element.
 */
public abstract class AbstractElement implements Element {

    /**
     * Size of the element.
     */
    private final Size size;

    /**
     * Position of the element.
     */
    private final Position position;

    public AbstractElement(Size size, Position position) {
        this.size = size;
        this.position = position;
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public Position getPosition() {
        return position;
    }

}
