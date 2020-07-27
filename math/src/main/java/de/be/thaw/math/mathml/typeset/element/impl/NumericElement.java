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

    /**
     * Kerning adjustments.
     */
    private final double[] kerningAdjustments;

    public NumericElement(String value, double fontSize, Size size, double[] kerningAdjustments, Position position) {
        super(position);

        setSize(size);
        this.value = value;
        this.fontSize = fontSize;
        this.kerningAdjustments = kerningAdjustments;
    }

    /**
     * Get the value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the font size to display the value with.
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
        return MathElementType.NUMERICAL;
    }

}
