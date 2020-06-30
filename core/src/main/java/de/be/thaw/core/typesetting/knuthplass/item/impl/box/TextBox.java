package de.be.thaw.core.typesetting.knuthplass.item.impl.box;

import de.be.thaw.core.typesetting.knuthplass.item.impl.Box;

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

    public TextBox(String text, double width) {
        this.text = text;
        this.width = width;
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

}
