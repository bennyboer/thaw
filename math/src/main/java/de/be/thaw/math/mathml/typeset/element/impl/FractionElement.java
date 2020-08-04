package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * Element representing a fraction.
 */
public class FractionElement extends AbstractMathElement {

    /**
     * Width of the fraction line.
     */
    private double lineWidth;

    /**
     * Spacing on each side of the fraction line.
     */
    private double lineSpacing;

    /**
     * Whether the fraction is bevelled (diagonal line) or normal (horizontal line).
     */
    private final boolean bevelled;

    public FractionElement(boolean bevelled, double lineWidth, double lineSpacing, Position position) {
        super(position);

        this.bevelled = bevelled;
        this.lineWidth = lineWidth;
        this.lineSpacing = lineSpacing;
    }

    /**
     * Check whether the fraction is bevelled (diagonal line) or normal (horizontal line).
     *
     * @return bevelled
     */
    public boolean isBevelled() {
        return bevelled;
    }

    /**
     * Get the spacing on each side of the fraction line.
     *
     * @return spacing
     */
    public double getLineSpacing() {
        return lineSpacing;
    }

    /**
     * Get the line width of the fraction line.
     *
     * @return line width
     */
    public double getLineWidth() {
        return lineWidth;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.FRACTION;
    }

    @Override
    public void scale(double factor) {
        super.scale(factor);

        lineWidth *= factor;
        lineSpacing *= factor;
    }

    @Override
    public double getBaseline() {
        double baseline = super.getBaseline();
        if (isBevelled()) {
            return (baseline + getSize().getHeight()) / 2;
        }

        double lineHeight = getChildren().orElseThrow().get(0).getSize().getHeight() + getLineSpacing() + getLineWidth() / 2;

        MathElement parent = getParent().orElseThrow();
        for (MathElement child : parent.getChildren().orElseThrow()) {
            if (!(child instanceof FractionElement)) {
                double otherBaseline = child.getBaseline();

                return lineHeight + otherBaseline / 2;
            }
        }

        return lineHeight + baseline / 2;
    }

}
