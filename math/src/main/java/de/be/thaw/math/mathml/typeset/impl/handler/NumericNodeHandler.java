package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.font.util.KernedSize;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.NumericNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.NumericElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.util.MathVariantUtil;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with numerical nodes.
 */
public class NumericNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mn";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        NumericNode mn = (NumericNode) node;

        String value = mn.getValue();

        // Convert value to the correct font variant (math variant)
        value = MathVariantUtil.convertStringUsingMathVariant(value, mn.getMathVariant());

        // TODO Deal with mathsize (once attribute is parsed)

        KernedSize size;
        try {
            size = ctx.getConfig().getFont().getKernedStringSize(-1, value, ctx.getLevelAdjustedFontSize());
        } catch (Exception e) {
            throw new TypesetException(e);
        }
        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth());

        return new NumericElement(value, ctx.getLevelAdjustedFontSize(), new Size(size.getWidth(), size.getAscent()), size.getKerningAdjustments(), position);
    }

}
