package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.util.Position;

/**
 * A vertical element (under, over, underover).
 */
public abstract class VerticalElement extends AbstractMathElement {

    public VerticalElement(Position position) {
        super(position);
    }

}
