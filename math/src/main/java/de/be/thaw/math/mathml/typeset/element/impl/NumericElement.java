package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A math element representation of an numeric element.
 */
public class NumericElement extends AbstractMathElement {

    /**
     * The value.
     */
    private final String value;

    /**
     * Font size of the element.
     */
    private final double fontSize;

    public NumericElement(String value, double fontSize, Size size, Position position) {
        super(position);

        setSize(size);
        this.value = value;
        this.fontSize = fontSize;
    }

    /**
     * Get the value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    public double getFontSize() {
        return fontSize;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.NUMERICAL;
    }

}
