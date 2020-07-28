package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.RowElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.util.Position;

/**
 * Handler dealing with the row node.
 */
public class RowNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mrow";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        RowElement row = new RowElement(new Position(ctx.getCurrentX(), ctx.getCurrentY()));

        // Save current position context for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset context to new relative position
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        for (MathMLNode child : node.getChildren()) {
            row.addChild(MathTypesetContext.getHandler(child.getName()).orElseThrow(() -> new TypesetException(String.format(
                    "Could not find a handler for the MathML node '%s'",
                    child.getName()
            ))).handle(child, ctx));

            ctx.setCurrentY(0);
        }

        // Vertical align children centered -> Get max middle y position of the row children first
        double maxMidY = 0;
        for (MathElement child : row.getChildren().orElseThrow()) {
            maxMidY = Math.max(maxMidY, child.getMidYPosition());
        }

        for (MathElement child : row.getChildren().orElseThrow()) {
            double midY = child.getMidYPosition();
            double diff = maxMidY - midY;

            child.setPosition(new Position(
                    child.getPosition().getX() - row.getPosition().getX(),
                    child.getPosition().getY() - row.getPosition().getY() + diff
            ));
        }

        // Set correct position context
        ctx.setCurrentX(oldX + row.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return row;
    }

}
