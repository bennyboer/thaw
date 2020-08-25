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

    /**
     * Whether the element is inline with text or a block.
     */
    private final boolean inline;

    /**
     * Baseline to use (if inline).
     */
    private final double baseline;

    public MathExpressionElement(MathExpression expression, int pageNumber, Size size, Position position, DocumentNode node, boolean inline, double baseline) {
        super(pageNumber, size, position);

        this.expression = expression;
        this.node = node;
        this.inline = inline;
        this.baseline = baseline;
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

    /**
     * Check if the element is inline with text or a block.
     *
     * @return inline
     */
    public boolean isInline() {
        return inline;
    }

    /**
     * Get the baseline to use.
     *
     * @return baseline
     */
    public double getBaseline() {
        return baseline;
    }

}
