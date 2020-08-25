package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;
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

    /**
     * Original node the math expression belongs to.
     */
    private final DocumentNode node;

    public MathBox(MathExpression expression, DocumentNode node) {
        this.expression = expression;
        this.node = node;
    }

    @Override
    public double getWidth() {
        return expression.getSize().getWidth();
    }

    public MathExpression getExpression() {
        return expression;
    }

    public DocumentNode getNode() {
        return node;
    }

}
