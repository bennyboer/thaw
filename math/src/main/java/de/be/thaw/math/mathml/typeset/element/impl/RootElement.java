package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A root element.
 */
public class RootElement extends AbstractMathElement {

    /**
     * The root line thickness.
     */
    private double lineThickness;

    public RootElement(Position position, double lineThickness) {
        super(position);

        this.lineThickness = lineThickness;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.ROOT;
    }

    @Override
    public double getMidYPosition() {
        // Get the mid y position of the basis element
        return getChildren().orElseThrow().get(0).getMidYPosition();
    }

    /**
     * Get the thickness of the root line.
     *
     * @return thickness
     */
    public double getLineThickness() {
        return lineThickness;
    }

    @Override
    public void scale(double factor) {
        super.scale(factor);

        lineThickness *= factor;
    }

}
