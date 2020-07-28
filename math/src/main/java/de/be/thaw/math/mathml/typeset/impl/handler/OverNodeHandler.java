package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.OverNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.OverElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.util.Position;

/**
 * Handler dealing with the over node.
 */
public class OverNodeHandler extends VerticalNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mover";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        OverNode overNode = (OverNode) node;

        double spacing = ctx.getLevelAdjustedFontSize() * RELATIVE_SPACING;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the over element
        ctx.setLevel(ctx.getLevel() + 2);
        MathElement overElement = MathTypesetContext.getHandler(overNode.getChildren().get(1).getName()).orElseThrow(() -> new TypesetException(String.format(
                "Could not find a handler for the MathML node '%s'",
                overNode.getChildren().get(1).getName()
        ))).handle(overNode.getChildren().get(1), ctx);
        ctx.setLevel(ctx.getLevel() - 2);

        // Shift the basis element
        ctx.setCurrentX(0);
        ctx.setCurrentY(overElement.getSize().getHeight() + spacing);

        // Then typeset the basis element
        MathElement basisElement = MathTypesetContext.getHandler(overNode.getChildren().get(0).getName()).orElseThrow(() -> new TypesetException(String.format(
                "Could not find a handler for the MathML node '%s'",
                overNode.getChildren().get(0).getName()
        ))).handle(overNode.getChildren().get(0), ctx);

        // Align elements according to the alignment attribute
        alignElements(overNode.getAlignment(), overElement, basisElement);

        // Create over element and add the children
        OverElement element = new OverElement(new Position(oldX, oldY));
        element.addChild(overElement);
        element.addChild(basisElement);

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return element;
    }

}
