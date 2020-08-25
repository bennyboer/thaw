package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A subscript element.
 */
public class SubscriptElement extends AbstractMathElement {

    public SubscriptElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.SUB_SCRIPT;
    }

    @Override
    public boolean isVerticalStretchy() {
        return getChildren().orElseThrow().get(0).isVerticalStretchy(); // Stretchy when the base element is!
    }

    @Override
    public void setStretchScaleY(double scaleY) {
        double maxHeight = getSize().getHeight() * scaleY; // Restore the original height to stretch to

        // Stretch the base element
        MathElement baseElement = getChildren().orElseThrow().get(0);
        double heightDiff = maxHeight - baseElement.getSize().getHeight();
        baseElement.setStretchScaleY(maxHeight / baseElement.getSize().getHeight());

        // Reposition subscript element
        MathElement subscriptElement = getChildren().orElseThrow().get(1);
        subscriptElement.setPosition(new Position(
                subscriptElement.getPosition(false).getX(),
                subscriptElement.getPosition(false).getY() + heightDiff
        ));
    }

}
