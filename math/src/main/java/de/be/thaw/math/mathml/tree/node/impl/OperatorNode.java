package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.util.operator.OperatorForm;

/**
 * Operator <mo> node.
 */
public class OperatorNode extends TokenNode {

    /**
     * Width of a space to the left of an arithmetic operator (if any).
     */
    private final double leftSpaceWidth;

    /**
     * Width of a space to the right of an arithmetic operator (if any).
     */
    private final double rightSpaceWidth;

    /**
     * Whether the operator should be displayed in a large version.
     */
    private final boolean largeOp;

    /**
     * Whether the operator is stretchy vertically.
     */
    private final boolean verticalStretchy;

    /**
     * Whether the operator is stretchy horizontally.
     */
    private final boolean horizontalStretchy;

    /**
     * Form of the operator.
     */
    private final OperatorForm form;

    public OperatorNode(String text, MathVariant mathVariant, double sizeFactor, double leftSpaceWidth, double rightSpaceWidth, boolean largeOp, boolean verticalStretchy, boolean horizontalStretchy, OperatorForm form) {
        super("mo", text, mathVariant, sizeFactor);

        this.leftSpaceWidth = leftSpaceWidth;
        this.rightSpaceWidth = rightSpaceWidth;

        this.largeOp = largeOp;
        this.verticalStretchy = verticalStretchy;
        this.horizontalStretchy = horizontalStretchy;

        this.form = form;
    }

    public double getLeftSpaceWidth() {
        return leftSpaceWidth;
    }

    public double getRightSpaceWidth() {
        return rightSpaceWidth;
    }

    public boolean isLargeOp() {
        return largeOp;
    }

    public boolean isVerticalStretchy() {
        return verticalStretchy;
    }

    public boolean isHorizontalStretchy() {
        return horizontalStretchy;
    }

    public OperatorForm getForm() {
        return form;
    }

}
