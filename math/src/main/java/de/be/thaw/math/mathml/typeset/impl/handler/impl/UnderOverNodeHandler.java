package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.UnderOverNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.UnderOverElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with the under and over node.
 */
public class UnderOverNodeHandler extends VerticalNodeHandler {

    @Override
    public String supportedNodeName() {
        return "munderover";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        UnderOverNode underOverNode = (UnderOverNode) node;

        double spacing = ctx.getLevelAdjustedFontSize() * RELATIVE_SPACING;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the basis element
        MathElement basisElement = MathNodeHandlers.getHandler(underOverNode.getChildren().get(0).getName())
                .handle(underOverNode.getChildren().get(0), ctx);

        // Update preferred size
        Size oldPreferredSize = ctx.getPreferredSize();
        ctx.setPreferredSize(new Size(basisElement.getSize().getWidth(), 0));

        // Then typeset the over element
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);
        ctx.setLevel(ctx.getLevel() + 2);
        MathElement overElement = MathNodeHandlers.getHandler(underOverNode.getChildren().get(2).getName())
                .handle(underOverNode.getChildren().get(2), ctx);
        ctx.setLevel(ctx.getLevel() - 2);

        // Shift the basis element to be below the over element
        basisElement.setPosition(new Position(
                basisElement.getPosition(false).getX(),
                overElement.getSize().getHeight() + spacing
        ));

        // Shift the under element
        ctx.setCurrentX(0);
        ctx.setCurrentY(overElement.getSize().getHeight() + basisElement.getSize().getHeight() + spacing * 2);

        // Then typeset the under element
        ctx.setLevel(ctx.getLevel() + 2);
        MathElement underElement = MathNodeHandlers.getHandler(underOverNode.getChildren().get(1).getName())
                .handle(underOverNode.getChildren().get(1), ctx);
        ctx.setLevel(ctx.getLevel() - 2);

        // Reset preferred size
        ctx.setPreferredSize(oldPreferredSize);

        // Stretch basis element to the current preferred size if it is stretchy horizontally
        if (basisElement.isHorizontalStretchy() && ctx.getPreferredSize().getWidth() > basisElement.getSize().getWidth()) {
            basisElement.setStretchScaleX(ctx.getPreferredSize().getWidth() / basisElement.getSize().getWidth());
        }

        // Stretch over element if it is stretchy horizontally (based on the basis element width)
        if (overElement.isHorizontalStretchy() && basisElement.getSize().getWidth() > overElement.getSize().getWidth()) {
            overElement.setStretchScaleX(basisElement.getSize().getWidth() / overElement.getSize().getWidth());
        }

        // Stretch under element if it is stretchy horizontally (based on the basis element width)
        if (underElement.isHorizontalStretchy() && basisElement.getSize().getWidth() > underElement.getSize().getWidth()) {
            underElement.setStretchScaleX(basisElement.getSize().getWidth() / underElement.getSize().getWidth());
        }

        // Align elements according to the alignment attribute
        alignElements(underOverNode.getAlignment(), overElement, basisElement, underElement);

        // Create under and over element and add the children
        UnderOverElement element = new UnderOverElement(new Position(oldX, oldY));
        element.addChild(overElement);
        element.addChild(basisElement);
        element.addChild(underElement);

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return element;
    }

}
