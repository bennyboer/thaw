package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.OverNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.OverElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

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

        // First typeset the basis element
        MathElement basisElement = MathNodeHandlers.getHandler(overNode.getChildren().get(0).getName())
                .handle(overNode.getChildren().get(0), ctx);

        // Update preferred size
        Size oldPreferredSize = ctx.getPreferredSize();
        ctx.setPreferredSize(new Size(basisElement.getSize().getWidth(), 0));

        // Then typeset the over element
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);
        ctx.setLevel(ctx.getLevel() + 2);
        MathElement overElement = MathNodeHandlers.getHandler(overNode.getChildren().get(1).getName())
                .handle(overNode.getChildren().get(1), ctx);
        ctx.setLevel(ctx.getLevel() - 2);

        // Reset preferred size
        ctx.setPreferredSize(oldPreferredSize);

        // Shift the basis element to be below the over element
        basisElement.setPosition(new Position(
                basisElement.getPosition(false).getX(),
                overElement.getSize().getHeight() + spacing
        ));

        // Stretch basis element to the current preferred size if it is stretchy horizontally
        if (basisElement.isHorizontalStretchy() && ctx.getPreferredSize().getWidth() > basisElement.getSize().getWidth()) {
            basisElement.setStretchScaleX(ctx.getPreferredSize().getWidth() / basisElement.getSize().getWidth());
        }

        // Stretch over element if it is stretchy horizontally (based on the basis element width)
        if (overElement.isHorizontalStretchy() && basisElement.getSize().getWidth() > overElement.getSize().getWidth()) {
            overElement.setStretchScaleX(basisElement.getSize().getWidth() / overElement.getSize().getWidth());
        }

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
