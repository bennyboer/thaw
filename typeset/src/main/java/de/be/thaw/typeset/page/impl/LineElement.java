package de.be.thaw.typeset.page.impl;

import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.color.Color;

/**
 * Element representing a line.
 */
public class LineElement extends AbstractElement {

    /**
     * Style of the line.
     */
    private final LineStyle style;

    /**
     * Width of the line.
     */
    private final double lineWidth;

    /**
     * Color of the line.
     */
    private final Color color;

    public LineElement(int pageNumber, Size size, Position position, LineStyle style, double lineWidth, Color color) {
        super(pageNumber, size, position);

        this.style = style;
        this.lineWidth = lineWidth;
        this.color = color;
    }

    /**
     * Get the style to draw the line in.
     *
     * @return style
     */
    public LineStyle getStyle() {
        return style;
    }

    /**
     * Get the width of the line.
     *
     * @return width
     */
    public double getLineWidth() {
        return lineWidth;
    }

    /**
     * Get the color of the line.
     *
     * @return color
     */
    public Color getColor() {
        return color;
    }

    @Override
    public ElementType getType() {
        return ElementType.LINE;
    }

}
