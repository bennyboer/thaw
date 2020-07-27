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

    /**
     * Kerning adjustments.
     */
    private final double[] kerningAdjustments;

    public OperatorElement(String operator, double fontSize, Size size, double[] kerningAdjustments, Position position) {
        super(position);

        setSize(size);
        this.operator = operator;
        this.fontSize = fontSize;
        this.kerningAdjustments = kerningAdjustments;
    }

    /**
     * Get the operator.
     *
     * @return operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Get the font size to display the operator with.
     *
     * @return font size
     */
    public double getFontSize() {
        return fontSize;
    }

    /**
     * Get kerning adjustments of the text.
     *
     * @return kerning adjustments
     */
    public double[] getKerningAdjustments() {
        return kerningAdjustments;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.OPERATOR;
    }

}
