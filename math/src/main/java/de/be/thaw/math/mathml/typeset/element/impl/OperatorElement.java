package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A math element representation of an operator.
 */
public class OperatorElement extends AbstractMathElement {

    /**
     * The operator.
     */
    private final String operator;

    /**
     * Font size of the element.
     */
    private final double fontSize;

    public OperatorElement(String operator, double fontSize, Size size, Position position) {
        super(position);

        setSize(size);
        this.operator = operator;
        this.fontSize = fontSize;
    }

    /**
     * Get the operator.
     *
     * @return operator
     */
    public String getOperator() {
        return operator;
    }

    public double getFontSize() {
        return fontSize;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.OPERATOR;
    }

}
