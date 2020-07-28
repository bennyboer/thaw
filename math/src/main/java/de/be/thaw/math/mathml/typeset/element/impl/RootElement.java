package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A root element.
 */
public class RootElement extends AbstractMathElement {

    public RootElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.ROOT;
    }

    @Override
    public double getMidYPosition() {
        // Get the mid y position of the basis element
        return getChildren().orElseThrow().get(0).getMidYPosition();
    }

}
