package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.tree.node.impl.FractionNode;
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

    public FractionElement(double lineWidth, double lineSpacing, Position position) {
        super(position);

        this.lineWidth = lineWidth;
        this.lineSpacing = lineSpacing;
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
        // The mid y for fraction elements is not really the middle,
        // but rather the offset of the numerator + line spacing + half line width
        return getChildren().orElseThrow().get(0).getSize().getHeight()
                + getLineSpacing()
                + getLineWidth() / 2;
    }
}
