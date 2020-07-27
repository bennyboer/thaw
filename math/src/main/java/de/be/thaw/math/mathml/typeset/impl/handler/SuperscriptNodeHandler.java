package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SuperscriptNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.SuperscriptElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.util.Position;

/**
 * Handler dealing with the superscript node.
 */
public class SuperscriptNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "msup";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        SuperscriptNode superscriptNode = (SuperscriptNode) node;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the base element
        MathElement baseElement = MathTypesetContext.getHandler(superscriptNode.getChildren().get(0).getName()).orElseThrow(() -> new TypesetException(String.format(
                "Could not find a handler for the MathML node '%s'",
                superscriptNode.getChildren().get(0).getName()
        ))).handle(superscriptNode.getChildren().get(0), ctx);

        // Shift the superscript element
        ctx.setLevel(ctx.getLevel() + 5);
        ctx.setCurrentX(baseElement.getSize().getWidth());
        ctx.setCurrentY(0);

        // Then typeset the superscript element
        MathElement superscriptElement = MathTypesetContext.getHandler(superscriptNode.getChildren().get(1).getName()).orElseThrow(() -> new TypesetException(String.format(
                "Could not find a handler for the MathML node '%s'",
                superscriptNode.getChildren().get(1).getName()
        ))).handle(superscriptNode.getChildren().get(1), ctx);

        // Reset level
        ctx.setLevel(ctx.getLevel() - 5);

        // Shift the position of the superscript element according to the superscriptShift attribute
        superscriptElement.setPosition(new Position(
                superscriptElement.getPosition().getX(),
                superscriptElement.getPosition().getY() - superscriptElement.getSize().getHeight() * superscriptNode.getSuperscriptShift()
        ));

        // Create superscript element and add the children
        SuperscriptElement element = new SuperscriptElement(new Position(oldX, oldY));
        element.addChild(baseElement);
        element.addChild(superscriptElement);

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return element;
    }

}
