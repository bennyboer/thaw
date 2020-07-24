package de.be.thaw.math.mathml.typeset;

import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.util.Size;

/**
 * A typeset math expression.
 */
public class MathExpression {

    /**
     * The root math element.
     */
    private final MathElement root;

    public MathExpression(MathElement root) {
        this.root = root;
    }

    public MathElement getRoot() {
        return root;
    }

    public Size getSize() {
        return root.getSize();
    }

}
