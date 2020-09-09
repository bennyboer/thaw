package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;

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
     * Metrics of the text.
     */
    private FontDetailsSupplier.StringMetrics metrics;

    public PageNumberPlaceholderBox(DocumentNode node) {
        super("", FontDetailsSupplier.StringMetrics.placeholder(), node);
    }

    /**
     * Set the value of the placeholder.
     *
     * @param value   the value to display
     * @param width   of the value
     * @param metrics of the value
     */
    public void set(String value, double width, FontDetailsSupplier.StringMetrics metrics) {
        this.value = value;
        this.width = width;
        this.metrics = metrics;
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
    public FontDetailsSupplier.StringMetrics getMetrics() {
        if (metrics == null) {
            return super.getMetrics();
        }

        return metrics;
    }

}
