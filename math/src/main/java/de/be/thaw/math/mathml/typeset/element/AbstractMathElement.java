package de.be.thaw.math.mathml.typeset.element;

import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * An abstract math element.
 */
public abstract class AbstractMathElement implements MathElement {

    /**
     * Size of the element.
     */
    private Size size;

    /**
     * Position of the element.
     */
    private Position position;

    public AbstractMathElement(Size size, Position position) {
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

    public void setSize(Size size) {
        this.size = size;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
