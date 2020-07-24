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
     * The original fraction node.
     */
    private final FractionNode fractionNode;

    /**
     * Width of the fraction line.
     */
    private final double lineWidth;

    public FractionElement(FractionNode node, double lineWidth, Position position) {
        super(position);

        this.fractionNode = node;
        this.lineWidth = lineWidth;
    }

    /**
     * Get the original fraction node.
     *
     * @return fraction node
     */
    public FractionNode getFractionNode() {
        return fractionNode;
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

}
