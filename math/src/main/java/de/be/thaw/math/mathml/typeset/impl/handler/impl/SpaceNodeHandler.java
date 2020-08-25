package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SpaceNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.SpaceElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.util.Position;

/**
 * Handler dealing with the space node.
 */
public class SpaceNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mspace";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        SpaceNode spaceNode = (SpaceNode) node;

        Position position = new Position(ctx.getCurrentX(), ctx.getCurrentY());

        ctx.setCurrentX(ctx.getCurrentX() + spaceNode.getWidth());

        return new SpaceElement(
                position,
                spaceNode.getWidth(),
                spaceNode.getHeight(),
                spaceNode.getDepth()
        );
    }

}
