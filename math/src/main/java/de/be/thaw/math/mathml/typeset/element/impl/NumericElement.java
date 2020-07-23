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

    public NumericElement(String value, Size size, Position position) {
        super(size, position);

        this.value = value;
    }

    /**
     * Get the value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.NUMERICAL;
    }

}
