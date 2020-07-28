package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * Element representing a fraction.
 */
public class FractionElement extends AbstractMathElement {

    /**
     * Width of the fraction line.
     */
    private final double lineWidth;

    /**
     * Spacing on each side of the fraction line.
     */
    private final double lineSpacing;

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
    public double getMidYPosition() {
        if (isBevelled()) {
            return super.getMidYPosition();
        }

        // The mid y for fraction elements is not really the middle,
        // but rather the offset of the numerator + line spacing + half line width
        return getChildren().orElseThrow().get(0).getSize().getHeight()
                + getLineSpacing()
                + getLineWidth() / 2;
    }
}
