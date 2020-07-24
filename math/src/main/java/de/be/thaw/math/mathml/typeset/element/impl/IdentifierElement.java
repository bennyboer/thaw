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

    public IdentifierElement(String identifier, double fontSize, Size size, Position position) {
        super(position);

        setSize(size);
        this.identifier = identifier;
        this.fontSize = fontSize;
    }

    /**
     * Get the identifier.
     *
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    public double getFontSize() {
        return fontSize;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.IDENTIFIER;
    }

}
