package de.be.thaw.typeset.page.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.Optional;

/**
 * Element containing text.
 * For example a single word.
 */
public class TextElement extends AbstractElement {

    /**
     * Text of the element.
     */
    private String text;

    /**
     * Metrics of the text.
     */
    private final FontDetailsSupplier.StringMetrics metrics;

    /**
     * The original node the text belongs to in the thaw document.
     * Can be used to derive the used font, style, etc.
     */
    private final DocumentNode node;

    /**
     * Baseline to use.
     */
    private final double baseline;

    public TextElement(
            String text,
            FontDetailsSupplier.StringMetrics metrics,
            DocumentNode node,
            int pageNumber,
            double baseline,
            Size size,
            Position position
    ) {
        super(pageNumber, size, position);

        this.text = text;
        this.metrics = metrics;
        this.node = node;
        this.baseline = baseline;
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
     * Set the elements text.
     *
     * @param text to set
     */
    public void setText(String text) {
        this.text = text;
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
     * Get the metrics of the text.
     *
     * @return metrics
     */
    public FontDetailsSupplier.StringMetrics getMetrics() {
        return metrics;
    }

    /**
     * Get the baseline to use.
     *
     * @return baseline
     */
    public double getBaseline() {
        return baseline;
    }

}
