package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.RootNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.RootElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with a root node.
 */
public class RootNodeHandler implements MathMLNodeHandler {

    /**
     * Margin from exponent to basis.
     */
    static final double EXPONENT_TO_BASIS_MARGIN = 0.3;

    /**
     * Vertical padding to add to the basis element under the root.
     * This value is multiplied with the font size.
     */
    static final double VERTICAL_PADDING = 0.2;

    /**
     * Horizontal padding to add to the basis element under the root.
     * This value is multiplied with the font size.
     */
    static final double HORIZONTAL_PADDING = 0.1;

    @Override
    public String supportedNodeName() {
        return "mroot";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        RootNode rootNode = (RootNode) node;

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the exponent element
        ctx.setLevel(ctx.getLevel() + 5);
        MathElement exponentElement = MathNodeHandlers.getHandler(rootNode.getChildren().get(1).getName())
                .handle(rootNode.getChildren().get(1), ctx);
        ctx.setLevel(ctx.getLevel() - 5);

        double verticalPadding = ctx.getConfig().getFontSize() * VERTICAL_PADDING;
        double horizontalPadding = ctx.getConfig().getFontSize() * HORIZONTAL_PADDING;

        ctx.setCurrentX(ctx.getCurrentX() + ctx.getCurrentX() * EXPONENT_TO_BASIS_MARGIN + horizontalPadding);
        ctx.setCurrentY(verticalPadding);

        // Then typeset the basis element
        MathElement basisElement = MathNodeHandlers.getHandler(rootNode.getChildren().get(0).getName())
                .handle(rootNode.getChildren().get(0), ctx);

        // Shift exponent element to be as low as possible based on the size of the basis element
        exponentElement.setPosition(new Position(
                exponentElement.getPosition().getX(),
                Math.max(exponentElement.getPosition().getY(), basisElement.getSize().getHeight() - exponentElement.getSize().getHeight() * 2.5)
        ));

        // Create root element and add the children
        RootElement element = new RootElement(new Position(oldX, oldY), rootNode.getLineThickness());
        element.addChild(basisElement);
        element.addChild(exponentElement);

        // Set the correct size of the root element (including horizontal padding)
        Size size = element.getSize();
        element.setSize(new Size(size.getWidth() + horizontalPadding, size.getHeight()));

        // Set new position to context
        ctx.setCurrentX(oldX + element.getSize().getWidth() + horizontalPadding);
        ctx.setCurrentY(oldY);

        return element;
    }

}
