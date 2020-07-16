package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;

/**
 * Box acting as a page number of the current page placeholder.
 */
public class PageNumberPlaceholderBox extends TextBox {

    /**
     * Page number as string to display.
     */
    private String value = "";

    /**
     * Width of the page number string.
     */
    private double width = 0;

    /**
     * Kerning adjustments of the page number string.
     */
    private double[] kerningAdjustments = new double[0];

    /**
     * Font size to display the text with.
     */
    private double fontSize = 1;

    public PageNumberPlaceholderBox(DocumentNode node) {
        super("", 0, 1, new double[0], node);
    }

    /**
     * Set the value of the placeholder.
     *
     * @param value              the value to display
     * @param width              of the value
     * @param kerningAdjustments of the value string
     * @param fontSize           to display the string with
     */
    public void set(String value, double width, double[] kerningAdjustments, double fontSize) {
        this.value = value;
        this.width = width;
        this.kerningAdjustments = kerningAdjustments;
        this.fontSize = fontSize;
    }

    @Override
    public String getText() {
        return value;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double[] getKerningAdjustments() {
        return kerningAdjustments;
    }

    @Override
    public double getFontSize() {
        return fontSize;
    }

}
