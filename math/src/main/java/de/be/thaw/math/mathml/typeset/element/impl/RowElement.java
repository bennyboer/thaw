package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A row element.
 */
public class RowElement extends AbstractMathElement {

    public RowElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.ROW;
    }

}
