package de.be.thaw.typeset.page.impl;

import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.color.Color;

/**
 * An rectangle element.
 */
public class RectangleElement extends AbstractElement {

    /**
     * Border radius to apply.
     * Where top means radius on the top to the left,
     * right means radius on the top to the right,
     * bottom means radius on the bottom to the right,
     * left means radius on the bottom to the left.
     */
    private Insets borderRadius = new Insets(0);

    /**
     * The fill color to use.
     */
    private Color fillColor = new Color(1.0, 1.0, 1.0);

    /**
     * The stroke colors to use.
     * One for each side in order top, right, bottom, left.
     */
    private Color[] strokeColors = new Color[]{
            new Color(0.0, 0.0, 0.0),
            new Color(0.0, 0.0, 0.0),
            new Color(0.0, 0.0, 0.0),
            new Color(0.0, 0.0, 0.0)
    };

    /**
     * Line styles to use for the border sides.
     * In order top, right, bottom, left.
     */
    private LineStyle[] borderStyles = new LineStyle[]{
            LineStyle.SOLID,
            LineStyle.SOLID,
            LineStyle.SOLID,
            LineStyle.SOLID
    };

    /**
     * Widths of the border sides.
     */
    private Insets borderWidths = new Insets(0);

    public RectangleElement(int pageNumber, Size size, Position position) {
        super(pageNumber, size, position);
    }

    /**
     * Get the border radius to apply.
     *
     * @return border radius
     */
    public Insets getBorderRadius() {
        return borderRadius;
    }

    /**
     * Set the border radius to apply.
     *
     * @param borderRadius to set
     */
    public void setBorderRadius(Insets borderRadius) {
        this.borderRadius = borderRadius;
    }

    /**
     * Get the fill color to use.
     *
     * @return fill color
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Set the fill color to use.
     *
     * @param fillColor to set
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Get the stroke colors to use.
     * In order top, right, bottom, left.
     *
     * @return stroke colors.
     */
    public Color[] getStrokeColors() {
        return strokeColors;
    }

    /**
     * Set the stroke colors to use in order top, right, bottom, left.
     *
     * @param strokeColors to use
     */
    public void setStrokeColors(Color[] strokeColors) {
        this.strokeColors = strokeColors;
    }

    /**
     * Set the top stroke color.
     *
     * @param color to set
     */
    public void setTopStrokeColor(Color color) {
        strokeColors[0] = color;
    }

    /**
     * Get the top stroke color.
     *
     * @return top stroke color
     */
    public Color getTopStrokeColor() {
        return strokeColors[0];
    }

    /**
     * Set the bottom stroke color.
     *
     * @param color to set
     */
    public void setBottomStrokeColor(Color color) {
        strokeColors[2] = color;
    }

    /**
     * Get the bottom stroke color.
     *
     * @return bottom stroke color
     */
    public Color getBottomStrokeColor() {
        return strokeColors[2];
    }

    /**
     * Set the left stroke color.
     *
     * @param color to set
     */
    public void setLeftStrokeColor(Color color) {
        strokeColors[3] = color;
    }

    /**
     * Get the left stroke color.
     *
     * @return left stroke color
     */
    public Color getLeftStrokeColor() {
        return strokeColors[3];
    }

    /**
     * Set the right stroke color.
     *
     * @param color to set
     */
    public void setRightStrokeColor(Color color) {
        strokeColors[1] = color;
    }

    /**
     * Get the right stroke color.
     *
     * @return right stroke color
     */
    public Color getRightStrokeColor() {
        return strokeColors[1];
    }

    /**
     * Set a stroke color to all sides.
     *
     * @param color to set
     */
    public void setStrokeColor(Color color) {
        setTopStrokeColor(color);
        setLeftStrokeColor(color);
        setBottomStrokeColor(color);
        setRightStrokeColor(color);
    }

    /**
     * Get the border widths.
     *
     * @return border widths
     */
    public Insets getBorderWidths() {
        return borderWidths;
    }

    /**
     * Set the border widths to use.
     *
     * @param borderWidths to use
     */
    public void setBorderWidths(Insets borderWidths) {
        this.borderWidths = borderWidths;
    }

    /**
     * Get the border styles to use for each side.
     * In order top, right, bottom, left.
     *
     * @return border styles
     */
    public LineStyle[] getBorderStyles() {
        return borderStyles;
    }

    /**
     * Set the border styles to use for each side.
     * In order top, right, bottom, left.
     *
     * @param borderStyles to use
     */
    public void setBorderStyles(LineStyle[] borderStyles) {
        this.borderStyles = borderStyles;
    }

    /**
     * Get the top border style.
     *
     * @return top border style
     */
    public LineStyle getTopBorderStyle() {
        return borderStyles[0];
    }

    /**
     * Set the top border style.
     *
     * @param style to set
     */
    public void setTopBorderStyle(LineStyle style) {
        borderStyles[0] = style;
    }

    /**
     * Get the bottom border style.
     *
     * @return bottom border style
     */
    public LineStyle getBottomBorderStyle() {
        return borderStyles[2];
    }

    /**
     * Set the bottom border style.
     *
     * @param style to set
     */
    public void setBottomBorderStyle(LineStyle style) {
        borderStyles[2] = style;
    }

    /**
     * Get the left border style.
     *
     * @return left border style
     */
    public LineStyle getLeftBorderStyle() {
        return borderStyles[3];
    }

    /**
     * Set the left border style.
     *
     * @param style to set
     */
    public void setLeftBorderStyle(LineStyle style) {
        borderStyles[3] = style;
    }

    /**
     * Get the right border style.
     *
     * @return right border style
     */
    public LineStyle getRightBorderStyle() {
        return borderStyles[1];
    }

    /**
     * Set the right border style.
     *
     * @param style to set
     */
    public void setRightBorderStyle(LineStyle style) {
        borderStyles[1] = style;
    }

    /**
     * Set a border style to all sides.
     *
     * @param style to set
     */
    public void setBorderStyle(LineStyle style) {
        setRightBorderStyle(style);
        setLeftBorderStyle(style);
        setTopBorderStyle(style);
        setBottomBorderStyle(style);
    }

    @Override
    public ElementType getType() {
        return ElementType.RECTANGLE;
    }

}
