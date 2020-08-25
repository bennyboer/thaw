package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
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
     * Metrics of the text.
     */
    private final FontDetailsSupplier.StringMetrics metrics;

    /**
     * The original node representing this text box in the thaw document model.
     */
    private final DocumentNode node;

    public TextBox(String text, FontDetailsSupplier.StringMetrics metrics, DocumentNode node) {
        this.text = text;
        this.metrics = metrics;
        this.node = node;
    }

    @Override
    public double getWidth() {
        return metrics.getWidth();
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
     * Get the metrics of the text.
     *
     * @return metrics
     */
    public FontDetailsSupplier.StringMetrics getMetrics() {
        return metrics;
    }

}
