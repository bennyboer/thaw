package de.be.thaw.math.mathml.typeset.element.impl;

import de.be.thaw.math.mathml.typeset.element.AbstractMathElement;
import de.be.thaw.math.mathml.typeset.element.MathElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

/**
 * A math element representation of plain text.
 */
public class TextElement extends AbstractMathElement {

    /**
     * The text.
     */
    private final String text;

    /**
     * Font size of the element.
     */
    private final double fontSize;

    public TextElement(String text, double fontSize, Size size, Position position) {
        super(position);

        setSize(size);
        this.text = text;
        this.fontSize = fontSize;
    }

    /**
     * Get the text of the element.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the font size of the elements text.
     *
     * @return font size
     */
    public double getFontSize() {
        return fontSize;
    }

    @Override
    public MathElementType getType() {
        return MathElementType.TEXT;
    }

}
