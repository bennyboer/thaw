package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A token element is a element without children and with text content.
 */
public abstract class TokenElement extends AbstractMathElement {

    /**
     * The text of the token element.
     */
    private final String text;

    /**
     * Font size of the element.
     */
    private final double fontSize;

    /**
     * Kerning adjustments.
     */
    private final double[] kerningAdjustments;

    /**
     * The offset from the y-position of the element where the text baseline is.
     */
    private final double baseline;

    public TokenElement(String text, Size size, double fontSize, double baseline, double[] kerningAdjustments, Position position) {
        super(position);

        setSize(size);
        this.text = text;
        this.fontSize = fontSize;
        this.baseline = baseline;
        this.kerningAdjustments = kerningAdjustments;
    }

    /**
     * Get the text.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the offset from the y-position of the element where the baselien is.
     *
     * @return baseline
     */
    public double getBaseline() {
        return baseline;
    }

    /**
     * Get the font size to display the text with.
     *
     * @return font size
     */
    public double getFontSize() {
        return fontSize;
    }

    /**
     * Get kerning adjustments of the text.
     *
     * @return kerning adjustments
     */
    public double[] getKerningAdjustments() {
        return kerningAdjustments;
    }

}
