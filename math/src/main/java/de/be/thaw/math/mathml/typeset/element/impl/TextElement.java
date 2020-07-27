package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A math element representation of plain text.
 */
public class TextElement extends TokenElement {

    public TextElement(String text, Size size, double fontSize, double baseline, double[] kerningAdjustments, Position position) {
        super(text, size, fontSize, baseline, kerningAdjustments, position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.TEXT;
    }

}
