package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.FractionNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.FractionElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * Handler dealing with the fraction node.
 */
public class FractionNodeHandler implements MathMLNodeHandler {

    /**
     * Relative vertical offset of both numerator and denominator when the fraction is bevelled.
     */
    private static final double BEVELLED_VERTICAL_OFFSET = 0.25;

    @Override
    public String supportedNodeName() {
        return "mfrac";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        FractionNode fractionNode = (FractionNode) node;

        if (fractionNode.isBevelled()) {
            return buildBevelled(fractionNode, ctx);
        } else {
            return buildDefault(fractionNode, ctx);
        }
    }

    /**
     * Build a bevelled fraction with horizontal line.
     *
     * @param node of the fraction
     * @param ctx  the typeset context
     * @return the built element
     * @throws TypesetException in case the fraction element could not be typeset
     */
    private MathElement buildBevelled(FractionNode node, MathTypesetContext ctx) throws TypesetException {
        ctx.setLevel(ctx.getLevel() + 2);

        double lineWidth = node.getLineThickness();
        double lineSpacing = ctx.getConfig().getFontSize() * 0.2; // Distance from either side of the fraction line (horizontally)

        // Apply some adjustments based on the level of the math node
        lineWidth = Math.max(lineWidth - ctx.getLevel() * 0.05, 0);
        lineSpacing = Math.max(lineSpacing - ctx.getLevel() * 0.2, 1.0);

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the numerator
        MathElement numerator = MathNodeHandlers.getHandler(node.getChildren().get(0).getName())
                .handle(node.getChildren().get(0), ctx);

        // Offset the denominator
        ctx.setCurrentX(numerator.getSize().getWidth() + lineSpacing * 2 + lineWidth);
        ctx.setCurrentY(0);

        // Then typeset the denominator
        MathElement denominator = MathNodeHandlers.getHandler(node.getChildren().get(1).getName())
                .handle(node.getChildren().get(1), ctx);

        // Fraction element height is determined by the bigger of the two.
        // Vertically align the smaller centered and offset both numerator and denominator a little.
        if (numerator.getSize().getHeight() < denominator.getSize().getHeight()) {
            double yDiff = denominator.getSize().getHeight() - numerator.getSize().getHeight();
            double yOffset = denominator.getSize().getHeight() * BEVELLED_VERTICAL_OFFSET;

            numerator.setPosition(new Position(
                    numerator.getPosition().getX(),
                    numerator.getPosition().getY() + yDiff / 2
            ));
            denominator.setPosition(new Position(
                    denominator.getPosition().getX(),
                    denominator.getPosition().getY() + yOffset
            ));
        } else {
            double yDiff = numerator.getSize().getHeight() - denominator.getSize().getHeight();
            double yOffset = numerator.getSize().getHeight() * BEVELLED_VERTICAL_OFFSET;

            denominator.setPosition(new Position(
                    denominator.getPosition().getX(),
                    denominator.getPosition().getY() + yDiff / 2 + yOffset
            ));
            numerator.setPosition(new Position(
                    numerator.getPosition().getX(),
                    numerator.getPosition().getY()
            ));
        }

        // Create fraction element and add nominator and denominator
        FractionElement fractionElement = new FractionElement(true, lineWidth, lineSpacing, new Position(oldX, oldY));
        fractionElement.addChild(numerator);
        fractionElement.addChild(denominator);

        // Set new position to context
        ctx.setCurrentX(oldX + fractionElement.getSize().getWidth());
        ctx.setCurrentY(oldY);

        ctx.setLevel(ctx.getLevel() - 2);

        return fractionElement;
    }

    /**
     * Build a default fraction with horizontal line.
     *
     * @param node of the fraction
     * @param ctx  the typeset context
     * @return the built element
     * @throws TypesetException in case the fraction element could not be typeset
     */
    private FractionElement buildDefault(FractionNode node, MathTypesetContext ctx) throws TypesetException {
        ctx.setLevel(ctx.getLevel() + 1);

        double lineWidth = node.getLineThickness();
        double lineSpacing = ctx.getConfig().getFontSize() * 0.2; // Distance from either side of the fraction line
        double horizontalPadding = lineSpacing; // Distance from either side for the fraction children

        // Apply some adjustments based on the level of the math node
        lineWidth = Math.max(lineWidth - ctx.getLevel() * 0.05, 0);
        lineSpacing = Math.max(lineSpacing - ctx.getLevel() * 0.2, 1.0);
        horizontalPadding = Math.max(horizontalPadding - ctx.getLevel() * 0.2, 1.0);

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(horizontalPadding);
        ctx.setCurrentY(0);

        // First typeset the numerator
        MathElement numerator = MathNodeHandlers.getHandler(node.getChildren().get(0).getName())
                .handle(node.getChildren().get(0), ctx);

        // Offset the denominator
        ctx.setCurrentX(horizontalPadding);
        ctx.setCurrentY(numerator.getSize().getHeight() + lineSpacing * 2 + lineWidth);

        // Then typeset the denominator
        MathElement denominator = MathNodeHandlers.getHandler(node.getChildren().get(1).getName())
                .handle(node.getChildren().get(1), ctx);

        // Change the position of the smaller element (numerator or denominator) to be either the numeratorAlignment or denominatorAlignment
        alignElements(numerator, denominator, node.getNumeratorAlignment(), node.getDenominatorAlignment());

        // Create fraction element and add nominator and denominator
        FractionElement fractionElement = new FractionElement(false, lineWidth, lineSpacing, new Position(oldX, oldY));
        fractionElement.addChild(numerator);
        fractionElement.addChild(denominator);

        // Set the correct size of the fraction element (including horizontalPadding)
        Size size = fractionElement.getSize();
        fractionElement.setSize(new Size(size.getWidth() + horizontalPadding * 2, size.getHeight()));

        // Set new position to context
        ctx.setCurrentX(oldX + fractionElement.getSize().getWidth());
        ctx.setCurrentY(oldY);

        ctx.setLevel(ctx.getLevel() - 1);

        return fractionElement;
    }

    /**
     * Align the passed elements accordingly to the passed alignments.
     *
     * @param numerator            the numerator element
     * @param denominator          the denominator element
     * @param numeratorAlignment   alignment of the numerator
     * @param denominatorAlignment alignment of the denominator
     */
    private void alignElements(
            MathElement numerator,
            MathElement denominator,
            HorizontalAlignment numeratorAlignment,
            HorizontalAlignment denominatorAlignment
    ) {
        // Find smaller and greater elements (in their width)
        MathElement smaller;
        MathElement greater;
        HorizontalAlignment smallerAlignment; // Alignment of the smaller element
        if (numerator.getSize().getWidth() < denominator.getSize().getWidth()) {
            smaller = numerator;
            greater = denominator;
            smallerAlignment = numeratorAlignment;
        } else if (numerator.getSize().getWidth() > denominator.getSize().getWidth()) {
            smaller = denominator;
            greater = numerator;
            smallerAlignment = denominatorAlignment;
        } else {
            return; // No need to align
        }

        // Align the smaller element according to the set alignment
        double xDiff = greater.getSize().getWidth() - smaller.getSize().getWidth();
        if (smallerAlignment == HorizontalAlignment.CENTER) {
            smaller.setPosition(new Position(
                    xDiff / 2 + smaller.getPosition().getX(),
                    smaller.getPosition().getY()
            ));
        } else if (smallerAlignment == HorizontalAlignment.RIGHT) {
            smaller.setPosition(new Position(
                    xDiff + smaller.getPosition().getX(),
                    smaller.getPosition().getY()
            ));
        }
    }

}
