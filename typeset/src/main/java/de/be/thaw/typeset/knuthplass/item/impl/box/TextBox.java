package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.item.impl.Box;

/**
 * Box containing text.
 */
public class TextBox extends Box {

    /**
     * The text this box is representing.
     */
    private final String text;

    /**
     * Width of the text.
     */
    private final double width;

    /**
     * Font size the box width was calculated with.
     */
    private final double fontSize;

    /**
     * Adjustments due to kerning per character in the text.
     */
    private final double[] kerningAdjustments;

    /**
     * The original node representing this text box in the thaw document model.
     */
    private final DocumentNode node;

    public TextBox(String text, double width, double fontSize, double[] kerningAdjustments, DocumentNode node) {
        this.text = text;
        this.width = width;
        this.fontSize = fontSize;
        this.kerningAdjustments = kerningAdjustments;
        this.node = node;
    }

    @Override
    public double getWidth() {
        return width;
    }

    /**
     * Get the text of the box.
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the original node representing this text box in the thaw document model.
     *
     * @return node
     */
    public DocumentNode getNode() {
        return node;
    }

    /**
     * Get the kerning adjustments per character.
     *
     * @return kerning adjustments
     */
    public double[] getKerningAdjustments() {
        return kerningAdjustments;
    }

    /**
     * Get the font size the boxes width was calculated with.
     *
     * @return font size
     */
    public double getFontSize() {
        return fontSize;
    }

}
