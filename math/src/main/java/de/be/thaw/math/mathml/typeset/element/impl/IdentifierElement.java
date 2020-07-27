package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A math element representation of an identifier.
 */
public class IdentifierElement extends AbstractMathElement {

    /**
     * The identifier.
     */
    private final String identifier;

    /**
     * Font size of the element.
     */
    private final double fontSize;

    /**
     * Kerning adjustments.
     */
    private final double[] kerningAdjustments;

    public IdentifierElement(String identifier, double fontSize, Size size, double[] kerningAdjustments, Position position) {
        super(position);

        setSize(size);
        this.identifier = identifier;
        this.fontSize = fontSize;
        this.kerningAdjustments = kerningAdjustments;
    }

    /**
     * Get the identifier.
     *
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get the font size of the elements identifier.
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

    @Override
    public MathElementType getType() {
        return MathElementType.IDENTIFIER;
    }

}
