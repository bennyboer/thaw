package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.font.util.CharacterSize;
import de.be.thaw.font.util.KernedSize;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.TextNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.TextElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.util.MathVariantUtil;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with identifier nodes.
 */
public class TextNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mtext";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        TextNode textNode
                = (TextNode) node;

        String text = textNode.getText();

        // Convert text to the correct font variant (math variant)
        text = MathVariantUtil.convertStringUsingMathVariant(text, textNode.getMathVariant());

        // TODO Deal with mathsize (once attribute is parsed)

        KernedSize size;
        try {
            size = ctx.getConfig().getFont().getKernedStringSize(-1, text, ctx.getLevelAdjustedFontSize());
        } catch (Exception e) {
            throw new TypesetException(e);
        }

        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth());

        return new TextElement(text, ctx.getLevelAdjustedFontSize(), new Size(size.getWidth(), size.getAscent()), size.getKerningAdjustments(), position);
    }

}
