package de.be.thaw.typeset.page.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

import java.util.Optional;

/**
 * Element containing text.
 * For example a single word.
 */
public class TextElement extends AbstractElement {

    /**
     * Text of the element.
     */
    private final String text;

    /**
     * Font size of the text in the text element.
     */
    private final double fontSize;

    /**
     * Adjustments due to kerning for each code point in the text.
     */
    private final double[] kerningAdjustments;

    /**
     * The original node the text belongs to in the thaw document.
     * Can be used to derive the used font, style, etc.
     */
    private final DocumentNode node;

    public TextElement(
            String text,
            double fontSize,
            double[] kerningAdjustments,
            DocumentNode node,
            int pageNumber,
            Size size,
            Position position
    ) {
        super(pageNumber, size, position);

        this.text = text;
        this.fontSize = fontSize;
        this.kerningAdjustments = kerningAdjustments;
        this.node = node;
    }

    /**
     * Get the text of the element.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    @Override
    public Optional<DocumentNode> getNode() {
        return Optional.of(node);
    }

    @Override
    public ElementType getType() {
        return ElementType.TEXT;
    }

    /**
     * Get the adjustments due to kerning.
     *
     * @return kerning adjustments
     */
    public double[] getKerningAdjustments() {
        return kerningAdjustments;
    }

    /**
     * Get the font size of the text in the element.
     *
     * @return font size
     */
    public double getFontSize() {
        return fontSize;
    }

}
