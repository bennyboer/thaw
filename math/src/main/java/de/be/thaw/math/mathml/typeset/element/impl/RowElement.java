package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A row element.
 */
public class RowElement extends AbstractMathElement {

    /**
     * The rows baseline.
     */
    private double baseline;

    public RowElement(Position position, double baseline) {
        super(position);

        this.baseline = baseline;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.ROW;
    }

    @Override
    public double getBaseline() {
        return baseline;
    }

    @Override
    public void setBaseline(double baseline) {
        this.baseline = baseline;
    }

    @Override
    public void scale(double factor) {
        super.scale(factor);

        baseline *= factor;
    }

}
