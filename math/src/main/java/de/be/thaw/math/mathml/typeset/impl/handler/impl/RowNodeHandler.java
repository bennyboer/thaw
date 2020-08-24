package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.RowElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.ArrayList;
import java.util.List;

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
        // Save current position context for later
        double oldX = ctx.getCurrentX();
        double oldY = ctx.getCurrentY();

        // Reset context to new relative position
        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        List<MathElement> children = new ArrayList<>();
        for (MathMLNode child : node.getChildren()) {
            children.add(MathNodeHandlers.getHandler(child.getName())
                    .handle(child, ctx));

            ctx.setCurrentY(0);
        }

        // Gather some metrics
        double maxBaseline = 0;
        double maxHeight = 0;
        for (MathElement child : children) {
            if (!child.isVerticalStretchy()) {
                maxBaseline = Math.max(maxBaseline, child.getBaseline());
                maxHeight = Math.max(maxHeight, child.getSize().getHeight());
            }
        }

        // Align children by their baseline
        for (MathElement child : children) {
            double baseline = child.getBaseline();
            double diff = maxBaseline - baseline;

            child.setPosition(new Position(
                    child.getPosition(false).getX(),
                    child.getPosition(false).getY() + diff
            ));
        }

        // Stretch vertical stretchy elements
        for (MathElement child : children) {
            if (child.isVerticalStretchy()) {
                child.setStretchScaleY(maxHeight / child.getSize().getHeight());
                child.setBaseline(child.getBaseline() * child.getStretchScaleY());
                child.setPosition(new Position(child.getPosition(false).getX(), 0));
                child.setSize(new Size(child.getSize().getWidth(), child.getSize().getHeight() * child.getStretchScaleY()));
            }
        }

        // Create row element
        RowElement row = new RowElement(new Position(oldX, oldY), maxBaseline);

        // Add children
        for (MathElement child : children) {
            row.addChild(child);
        }

        // Set correct position context
        ctx.setCurrentX(oldX + row.getSize().getWidth());
        ctx.setCurrentY(oldY);

        return row;
    }

}
