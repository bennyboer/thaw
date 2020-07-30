package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.Position;

/**
 * Handler for vertical nodes.
 */
public abstract class VerticalNodeHandler implements MathMLNodeHandler {

    /**
     * Relative spacing between the different vertically stacked elements.
     */
    protected static final double RELATIVE_SPACING = 0.1;

    /**
     * Align the passed elements.
     *
     * @param alignment to apply
     * @param elements  to align
     */
    protected void alignElements(HorizontalAlignment alignment, MathElement... elements) {
        double maxWidth = Double.MIN_VALUE;
        for (MathElement element : elements) {
            maxWidth = Math.max((element.getPosition().getX() + element.getSize().getWidth()), maxWidth);
        }

        for (MathElement element : elements) {
            double width = element.getPosition().getX() + element.getSize().getWidth();
            double xDiff = maxWidth - width;

            if (alignment == HorizontalAlignment.CENTER) {
                element.setPosition(new Position(
                        element.getPosition().getX() + xDiff / 2,
                        element.getPosition().getY()
                ));
            } else if (alignment == HorizontalAlignment.RIGHT) {
                element.setPosition(new Position(
                        element.getPosition().getX() + xDiff,
                        element.getPosition().getY()
                ));
            }
        }
    }

}
