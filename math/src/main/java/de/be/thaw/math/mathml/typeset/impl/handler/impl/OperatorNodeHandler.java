package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.font.util.KernedSize;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.OperatorNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.OperatorElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.util.MathVariantUtil;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with operator nodes.
 */
public class OperatorNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mo";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        OperatorNode mo = (OperatorNode) node;

        String operator = mo.getText();

        // Convert operator to the correct font variant (math variant)
        operator = MathVariantUtil.convertStringUsingMathVariant(operator, mo.getMathVariant());

        // Deal with the size factor to scale the text (MathML attribute mathsize)
        double fontSize = ctx.getLevelAdjustedFontSize() * mo.getSizeFactor();

        KernedSize size;
        try {
            size = ctx.getConfig().getFont().getKernedStringSize(-1, operator, fontSize);
        } catch (Exception e) {
            throw new TypesetException(e);
        }

        double leftMargin = mo.getLeftSpaceWidth() * Math.max(0, 1.0 - 0.25 * ctx.getLevel());
        double rightMargin = mo.getRightSpaceWidth() * Math.max(0, 1.0 - 0.25 * ctx.getLevel());

        Position position = new Position(ctx.getCurrentX() + leftMargin, ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth() + rightMargin);

        return new OperatorElement(operator, new Size(size.getWidth(), size.getHeight()), fontSize, size.getAscent(), size.getKerningAdjustments(), position);
    }

}
