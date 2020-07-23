package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.math.mathml.typeset.MathExpression;
import de.be.thaw.typeset.knuthplass.item.impl.Box;

/**
 * Box containing a math expression.
 */
public class MathBox extends Box {

    /**
     * The math expression.
     */
    private final MathExpression expression;

    public MathBox(MathExpression expression) {
        this.expression = expression;
    }

    @Override
    public double getWidth() {
        return expression.getSize().getWidth();
    }

}
