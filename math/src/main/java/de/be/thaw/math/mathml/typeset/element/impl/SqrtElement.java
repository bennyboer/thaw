package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A square root element.
 */
public class SqrtElement extends RootElement {

    public SqrtElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.SQUARE_ROOT;
    }

}
