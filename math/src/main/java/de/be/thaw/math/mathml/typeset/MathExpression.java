package de.be.thaw.math.mathml.typeset;

import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.util.Size;

import java.util.List;

/**
 * A typeset math expression.
 */
public class MathExpression {

    /**
     * Elements the expression consists of.
     */
    private final List<MathElement> elements;

    /**
     * Size of the math expression.
     */
    private final Size size;

    public MathExpression(List<MathElement> elements, Size size) {
        this.elements = elements;
        this.size = size;
    }

    public List<MathElement> getElements() {
        return elements;
    }

    public Size getSize() {
        return size;
    }

}
