package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A math element representation of an numeric element.
 */
public class NumericElement extends TokenElement {

    public NumericElement(String text, Size size, double fontSize, double baseline, double[] kerningAdjustments, Position position) {
        super(text, size, fontSize, baseline, kerningAdjustments, position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.NUMERICAL;
    }

}
