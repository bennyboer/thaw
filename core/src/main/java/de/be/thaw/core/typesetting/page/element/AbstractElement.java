package de.be.thaw.core.typesetting.page.element;

import de.be.thaw.core.util.Position;
import de.be.thaw.core.util.Size;

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
