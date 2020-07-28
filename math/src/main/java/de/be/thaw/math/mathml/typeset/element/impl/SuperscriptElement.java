package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A superscript element.
 */
public class SuperscriptElement extends AbstractMathElement {

    public SuperscriptElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.SUPER_SCRIPT;
    }

    @Override
    public double getMidYPosition() {
        return getChildren().orElseThrow().get(0).getMidYPosition();
    }

}
