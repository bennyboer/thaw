package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SubscriptNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.SubscriptElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;

/**
 * Handler dealing with the subscript node.
 */
public class SubscriptNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "msub";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        SubscriptNode subscriptNode = (SubscriptNode) node;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the base element
        MathElement baseElement = MathNodeHandlers.getHandler(subscriptNode.getChildren().get(0).getName())
                .handle(subscriptNode.getChildren().get(0), ctx);

        // Shift the subscript element
        ctx.setLevel(ctx.getLevel() + 3);
        ctx.setCurrentX(baseElement.getSize().getWidth());
        ctx.setCurrentY(0);

        // Then typeset the subscript element
        MathElement subscriptElement = MathNodeHandlers.getHandler(subscriptNode.getChildren().get(1).getName())
                .handle(subscriptNode.getChildren().get(1), ctx);

        // Reset level
        ctx.setLevel(ctx.getLevel() - 3);

        // Shift the position of the subscript element according to the subscriptShift attribute
        subscriptElement.setPosition(new Position(
                subscriptElement.getPosition().getX(),
                baseElement.getSize().getHeight() - subscriptElement.getSize().getHeight() + subscriptElement.getSize().getHeight() * subscriptNode.getSubscriptShift()
        ));

        // Create subscript element and add the children
        SubscriptElement element = new SubscriptElement(new Position(oldX, oldY));
        element.addChild(baseElement);
        element.addChild(subscriptElement);

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return element;
    }

}
