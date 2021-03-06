package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.NumericNode;
import de.be.thaw.math.mathml.tree.node.impl.SqrtNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.NumericElement;
import de.be.thaw.math.mathml.typeset.element.impl.SqrtElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with a root node.
 */
public class SqrtNodeHandler extends RootNodeHandler {

    @Override
    public String supportedNodeName() {
        return "msqrt";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        SqrtNode sqrtNode = (SqrtNode) node;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the fake exponent element (only to get the size of it basically).
        ctx.setLevel(ctx.getLevel() + 5);
        NumericElement exponentElement = (NumericElement) MathNodeHandlers.getHandler("mn")
                .handle(new NumericNode("2", MathVariant.NORMAL, 1.0), ctx);
        ctx.setLevel(ctx.getLevel() - 5);

        double verticalPadding = ctx.getConfig().getFontSize() * RootNodeHandler.VERTICAL_PADDING;
        double horizontalPadding = ctx.getConfig().getFontSize() * RootNodeHandler.HORIZONTAL_PADDING;
        ctx.setCurrentX(ctx.getCurrentX() + ctx.getCurrentX() * RootNodeHandler.EXPONENT_TO_BASIS_MARGIN + horizontalPadding);
        ctx.setCurrentY(verticalPadding);

        // Then typeset the basis element
        MathElement basisElement = MathNodeHandlers.getHandler(sqrtNode.getChildren().get(0).getName())
                .handle(sqrtNode.getChildren().get(0), ctx);

        // Shift exponent element to be as low as possible based on the size of the basis element
        exponentElement.setPosition(new Position(
                exponentElement.getPosition().getX(),
                Math.max(exponentElement.getPosition().getY(), basisElement.getSize().getHeight() - exponentElement.getSize().getHeight() * 2.5)
        ));

        // Create root element and add the children
        SqrtElement element = new SqrtElement(new Position(oldX, oldY), sqrtNode.getLineThickness());
        element.addChild(basisElement);

        // Add fake exponent element
        element.addChild(new NumericElement(" ", exponentElement.getSize(), exponentElement.getFontSize(), exponentElement.getBaseline(), exponentElement.getKerningAdjustments(), exponentElement.getPosition()));

        // Set the correct size of the root element (including horizontal padding)
        Size size = element.getSize();
        element.setSize(new Size(size.getWidth() + horizontalPadding, size.getHeight()));

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth() + verticalPadding);
        ctx.setCurrentY(oldY);

        return element;
    }

}
