package de.be.thaw.typeset.page.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.math.mathml.typeset.MathExpression;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.Optional;

/**
 * Element representing a math expression.
 */
public class MathExpressionElement extends AbstractElement {

    /**
     * The math expression.
     */
    private final MathExpression expression;

    /**
     * The original document node of the math expression.
     */
    private final DocumentNode node;

    public MathExpressionElement(MathExpression expression, int pageNumber, Size size, Position position, DocumentNode node) {
        super(pageNumber, size, position);

        this.expression = expression;
        this.node = node;
    }

    @Override
    public ElementType getType() {
        return ElementType.MATH;
    }

    /**
     * Get the math expression.
     *
     * @return expression
     */
    public MathExpression getExpression() {
        return expression;
    }

    @Override
    public Optional<DocumentNode> getNode() {
        return Optional.of(node);
    }

}
