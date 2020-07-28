package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A subsupscript element.
 */
public class SubsuperscriptElement extends AbstractMathElement {

    public SubsuperscriptElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.SUB_SUPER_SCRIPT;
    }

}
