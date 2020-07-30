package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A space element.
 */
public class SpaceElement extends AbstractMathElement {

    /**
     * Width of the space node.
     */
    private final double width;

    /**
     * Height of the space node (baseline).
     */
    private final double height;

    /**
     * Depth of the space node (under the baseline).
     */
    private final double depth;

    /**
     * Temporary generated size object from the objects attributes.
     */
    private Size tmpSize;

    public SpaceElement(Position position, double width, double height, double depth) {
        super(position);

        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.SPACE;
    }

    @Override
    public double getBaseline() {
        return getHeight();
    }

    /**
     * Get the width of the space.
     *
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get the height (baseline) of the space.
     *
     * @return height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Get the depth of the space (height under the baseline).
     *
     * @return depth
     */
    public double getDepth() {
        return depth;
    }

    @Override
    public Size getSize() {
        if (tmpSize == null) {
            tmpSize = new Size(
                    getWidth(),
                    getHeight() + getDepth()
            );
        }

        return tmpSize;
    }

}
