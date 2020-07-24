package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.IdentifierNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.IdentifierElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
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

        // TODO Deal with different font variants (mathvariants)

        Size size;
        try {
            size = ctx.getConfig().getFont().getStringSize(text, ctx.getConfig().getFontSize());
        } catch (Exception e) {
            throw new TypesetException(e);
        }

        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth());

        return new IdentifierElement(text, size, position);
    }

}
