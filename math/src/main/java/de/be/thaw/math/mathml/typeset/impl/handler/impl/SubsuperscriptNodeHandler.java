package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SubsuperscriptNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.SubsuperscriptElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;

/**
 * Handler dealing with the subsuperscriot node.
 */
public class SubsuperscriptNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "msubsup";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        SubsuperscriptNode subsuperscriptNode = (SubsuperscriptNode) node;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the base element
        MathElement baseElement = MathNodeHandlers.getHandler(subsuperscriptNode.getChildren().get(0).getName())
                .handle(subsuperscriptNode.getChildren().get(0), ctx);

        // Shift the subscript element
        ctx.setLevel(ctx.getLevel() + 3);
        ctx.setCurrentX(baseElement.getSize().getWidth());
        ctx.setCurrentY(0);

        // Then typeset the subscript element
        MathElement subscriptElement = MathNodeHandlers.getHandler(subsuperscriptNode.getChildren().get(1).getName())
                .handle(subsuperscriptNode.getChildren().get(1), ctx);

        ctx.setCurrentX(baseElement.getSize().getWidth());
        ctx.setCurrentY(0);

        // Then typeset the superscript element
        MathElement superscriptElement = MathNodeHandlers.getHandler(subsuperscriptNode.getChildren().get(2).getName())
                .handle(subsuperscriptNode.getChildren().get(2), ctx);

        // Reset level
        ctx.setLevel(ctx.getLevel() - 3);

        // Shift the position of the subscript element according to the subscriptShift attribute
        subscriptElement.setPosition(new Position(
                subscriptElement.getPosition().getX(),
                baseElement.getSize().getHeight() - subscriptElement.getSize().getHeight() + subscriptElement.getSize().getHeight() * subsuperscriptNode.getSubscriptShift()
        ));

        // Shift the position of the superscript element according to the superscriptShift attribute
        superscriptElement.setPosition(new Position(
                superscriptElement.getPosition().getX(),
                superscriptElement.getPosition().getY() - superscriptElement.getSize().getHeight() * subsuperscriptNode.getSuperscriptShift()
        ));

        // Create subsuperscript element and add the children
        SubsuperscriptElement element = new SubsuperscriptElement(new Position(oldX, oldY));
        element.addChild(baseElement);
        element.addChild(subscriptElement);
        element.addChild(superscriptElement);

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return element;
    }

}
