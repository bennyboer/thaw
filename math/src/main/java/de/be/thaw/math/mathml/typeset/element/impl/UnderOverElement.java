package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A over element.
 */
public class UnderOverElement extends VerticalElement {

    public UnderOverElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.UNDER_OVER;
    }

    @Override
    public double getBaseline() {
        return getChildren().orElseThrow().get(1).getPosition(false).getY() + getChildren().orElseThrow().get(1).getBaseline();
    }

}
