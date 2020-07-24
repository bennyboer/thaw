package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.FractionNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.FractionElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.util.Position;

/**
 * Handler dealing with the fraction node.
 */
public class FractionHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mfrac";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        FractionNode fractionNode = (FractionNode) node;

        double lineWidth = 1.0; // TODO Set later from the fraction node attribute!
        double lineSpacing = ctx.getConfig().getFontSize() * 0.1; // Distance from either side of the fraction line

        // Save current position for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset to new relative position context
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First typeset the numerator
        MathElement numerator = MathTypesetContext.getHandler(fractionNode.getChildren().get(0).getName()).orElseThrow(() -> new TypesetException(String.format(
                "Could not find a handler for the MathML node '%s'",
                fractionNode.getChildren().get(0).getName()
        ))).handle(fractionNode.getChildren().get(0), ctx);

        // Offset the denominator
        ctx.setCurrentX(0);
        ctx.setCurrentY(numerator.getSize().getHeight() + lineSpacing * 2 + lineWidth);

        // Then typeset the denominator
        MathElement denominator = MathTypesetContext.getHandler(fractionNode.getChildren().get(1).getName()).orElseThrow(() -> new TypesetException(String.format(
                "Could not find a handler for the MathML node '%s'",
                fractionNode.getChildren().get(1).getName()
        ))).handle(fractionNode.getChildren().get(1), ctx);

        // Change the position of the smaller element (numerator or denominator) to be centered
        if (numerator.getSize().getWidth() < denominator.getSize().getWidth()) {
            double xOffset = (denominator.getSize().getWidth() - numerator.getSize().getWidth()) / 2;
            numerator.setPosition(new Position(
                    xOffset + numerator.getPosition().getX(),
                    numerator.getPosition().getY()
            ));
        } else {
            double xOffset = (numerator.getSize().getWidth() - denominator.getSize().getWidth()) / 2;
            denominator.setPosition(new Position(
                    xOffset + denominator.getPosition().getX(),
                    denominator.getPosition().getY()
            ));
        }

        // Create fraction element and add nominator and denominator
        FractionElement fractionElement = new FractionElement(fractionNode, lineWidth, new Position(oldX, oldY));
        fractionElement.addChild(numerator);
        fractionElement.addChild(denominator);

        // Set new position to context
        ctx.setCurrentX(oldX + fractionElement.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return fractionElement;
    }

}
