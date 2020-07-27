package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.font.util.KernedSize;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.IdentifierNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.IdentifierElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.util.MathVariantUtil;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with identifier nodes.
 */
public class IdentifierNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mi";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        IdentifierNode mi = (IdentifierNode) node;

        String text = mi.getText();

        // Convert text to the correct font variant (math variant)
        text = MathVariantUtil.convertStringUsingMathVariant(text, mi.getMathVariant());

        // TODO Deal with mathsize (once attribute is parsed)

        KernedSize size;
        try {
            size = ctx.getConfig().getFont().getKernedStringSize(-1, text, ctx.getLevelAdjustedFontSize());
        } catch (Exception e) {
            throw new TypesetException(e);
        }

        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth());

        return new IdentifierElement(text, new Size(size.getWidth(), size.getHeight()), ctx.getLevelAdjustedFontSize(), size.getAscent(), size.getKerningAdjustments(), position);
    }

}
