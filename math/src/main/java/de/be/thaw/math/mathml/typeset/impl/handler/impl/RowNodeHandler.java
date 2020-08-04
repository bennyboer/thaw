package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.RowElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
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
            row.addChild(MathNodeHandlers.getHandler(child.getName())
                    .handle(child, ctx));

            ctx.setCurrentY(0);
        }

        // Align children by their baseline
        double maxBaseline = 0;
        for (MathElement child : row.getChildren().orElseThrow()) {
            maxBaseline = Math.max(maxBaseline, child.getBaseline());
        }

        for (MathElement child : row.getChildren().orElseThrow()) {
            double baseline = child.getBaseline();
            double diff = maxBaseline - baseline;

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
