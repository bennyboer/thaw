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

        String value = mn.getText();

        // Convert value to the correct font variant (math variant)
        value = MathVariantUtil.convertStringUsingMathVariant(value, mn.getMathVariant());

        // Deal with the size factor to scale the text (MathML attribute mathsize)
        double fontSize = ctx.getLevelAdjustedFontSize() * mn.getSizeFactor();

        KernedSize size;
        try {
            size = ctx.getConfig().getFont().getKernedStringSize(-1, value, fontSize);
        } catch (Exception e) {
            throw new TypesetException(e);
        }
        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth());

        return new NumericElement(value, new Size(size.getWidth(), size.getAscent()), fontSize, size.getAscent(), size.getKerningAdjustments(), position);
    }

}
