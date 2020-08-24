package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.tree.util.operator.OperatorForm;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A math element representation of an operator.
 */
public class OperatorElement extends TokenElement {

    /**
     * Whether to show a large operator version (if any).
     */
    private final boolean largeOp;

    /**
     * Form of the operator.
     */
    private final OperatorForm form;

    /**
     * Whether the operator is vertically stretchy.
     */
    private final boolean verticalStretchy;

    /**
     * Whether the operator is horizontally stretchy.
     */
    private final boolean horizontalStretchy;

    public OperatorElement(String text, Size size, double fontSize, double baseline, double[] kerningAdjustments, boolean largeOp, boolean verticalStretchy, boolean horizontalStretchy, OperatorForm form, Position position) {
        super(text, size, fontSize, baseline, kerningAdjustments, position);

        this.largeOp = largeOp;
        this.form = form;
        this.verticalStretchy = verticalStretchy;
        this.horizontalStretchy = horizontalStretchy;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.OPERATOR;
    }

    public boolean isLargeOp() {
        return largeOp;
    }

    public OperatorForm getForm() {
        return form;
    }

    @Override
    public boolean isVerticalStretchy() {
        return verticalStretchy;
    }

    @Override
    public boolean isHorizontalStretchy() {
        return horizontalStretchy;
    }

}
