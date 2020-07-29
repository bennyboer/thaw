package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A over element.
 */
public class UnderElement extends VerticalElement {

    public UnderElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.UNDER;
    }

    @Override
    public double getMidYPosition() {
        return getChildren().orElseThrow().get(0).getMidYPosition();
    }

    @Override
    public double getBaseline() {
        return getChildren().orElseThrow().get(0).getBaseline();
    }

}
