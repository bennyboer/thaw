package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;

/**
 * A superscript element.
 */
public class SuperscriptElement extends AbstractMathElement {

    public SuperscriptElement(Position position) {
        super(position);
    }

    @Override
    public MathElementType getType() {
        return MathElementType.SUPER_SCRIPT;
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
        baseElement.setStretchScaleY(maxHeight / baseElement.getSize().getHeight());
    }

}
