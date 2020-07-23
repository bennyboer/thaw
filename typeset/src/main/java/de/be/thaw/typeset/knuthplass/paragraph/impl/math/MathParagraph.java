package de.be.thaw.typeset.knuthplass.paragraph.impl.math;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.math.mathml.typeset.MathExpression;
import de.be.thaw.style.model.style.text.TextAlignment;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;

/**
 * Paragraph representing a math expression.
 */
public class MathParagraph extends AbstractParagraph {

    /**
     * The math expression.
     */
    private final MathExpression expression;

    /**
     * Alignment of the paragraph.
     */
    private final TextAlignment alignment;

    public MathParagraph(double lineWidth, DocumentNode node, MathExpression expression, TextAlignment alignment) {
        super(lineWidth, node);

        this.expression = expression;
        this.alignment = alignment;
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
    public ParagraphType getType() {
        return ParagraphType.MATH;
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

}
