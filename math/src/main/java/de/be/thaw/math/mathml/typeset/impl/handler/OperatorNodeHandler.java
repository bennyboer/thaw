package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.OperatorNode;
import de.be.thaw.math.mathml.typeset.element.impl.OperatorElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
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
    public void handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        OperatorNode mo = (OperatorNode) node;

        // TODO Deal with different font variants (mathvariants)

        Size size;
        try {
            size = ctx.getConfig().getFont().getStringSize(mo.getOperator(), ctx.getConfig().getFontSize());
        } catch (Exception e) {
            throw new TypesetException(e);
        }

        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth());

        ctx.pushElement(new OperatorElement(mo.getOperator(), size, position));
    }

}
