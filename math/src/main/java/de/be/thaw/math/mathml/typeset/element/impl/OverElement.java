package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A over element.
 */
public class OverElement extends VerticalElement {

    public OverElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.OVER;
    }

    @Override
    public double getMidYPosition() {
        return getChildren().orElseThrow().get(1).getMidYPosition();
    }

}
