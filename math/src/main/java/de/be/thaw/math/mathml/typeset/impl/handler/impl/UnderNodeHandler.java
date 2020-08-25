package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.UnderNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.UnderElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with the under node.
 */
public class UnderNodeHandler extends VerticalNodeHandler {

    @Override
    public String supportedNodeName() {
        return "munder";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        UnderNode underNode = (UnderNode) node;

        double spacing = ctx.getLevelAdjustedFontSize() * RELATIVE_SPACING;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the basis element
        MathElement basisElement = MathNodeHandlers.getHandler(underNode.getChildren().get(0).getName())
                .handle(underNode.getChildren().get(0), ctx);

        // Update preferred size
        Size oldPreferredSize = ctx.getPreferredSize();
        ctx.setPreferredSize(new Size(basisElement.getSize().getWidth(), 0));

        // Shift the under element
        ctx.setCurrentX(0);
        ctx.setCurrentY(basisElement.getSize().getHeight() + spacing);

        // Then typeset the under element
        ctx.setLevel(ctx.getLevel() + 2);
        MathElement underElement = MathNodeHandlers.getHandler(underNode.getChildren().get(1).getName())
                .handle(underNode.getChildren().get(1), ctx);
        ctx.setLevel(ctx.getLevel() - 2);

        // Reset preferred size
        ctx.setPreferredSize(oldPreferredSize);

        // Stretch basis element to the current preferred size if it is stretchy horizontally
        if (basisElement.isHorizontalStretchy() && ctx.getPreferredSize().getWidth() > basisElement.getSize().getWidth()) {
            basisElement.setStretchScaleX(ctx.getPreferredSize().getWidth() / basisElement.getSize().getWidth());
        }

        // Stretch under element if it is stretchy horizontally (based on the basis element width)
        if (underElement.isHorizontalStretchy() && basisElement.getSize().getWidth() > underElement.getSize().getWidth()) {
            underElement.setStretchScaleX(basisElement.getSize().getWidth() / underElement.getSize().getWidth());
        }

        // Align elements according to the alignment attribute
        alignElements(underNode.getAlignment(), basisElement, underElement);

        // Create under element and add the children
        UnderElement element = new UnderElement(new Position(oldX, oldY));
        element.addChild(basisElement);
        element.addChild(underElement);

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return element;
    }

}
