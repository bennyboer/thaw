package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A subscript element.
 */
public class SubscriptElement extends AbstractMathElement {

    public SubscriptElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.SUB_SCRIPT;
    }

}
