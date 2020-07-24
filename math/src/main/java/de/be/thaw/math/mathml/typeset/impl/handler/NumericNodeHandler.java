package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.NumericNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.NumericElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
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

        // TODO Deal with different font variants (mathvariants)

        Size size;
        try {
            size = ctx.getConfig().getFont().getStringSize(mn.getValue(), ctx.getConfig().getFontSize());
        } catch (Exception e) {
            throw new TypesetException(e);
        }

        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth());

        return new NumericElement(mn.getValue(), size, position);
    }

}
