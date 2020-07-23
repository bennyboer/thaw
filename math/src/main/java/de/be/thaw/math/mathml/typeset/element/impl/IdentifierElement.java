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

    public IdentifierElement(String identifier, Size size, Position position) {
        super(size, position);

        this.identifier = identifier;
    }

    /**
     * Get the identifier.
     *
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.IDENTIFIER;
    }

}
