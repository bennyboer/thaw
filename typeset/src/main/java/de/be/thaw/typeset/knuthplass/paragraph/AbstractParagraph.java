package de.be.thaw.typeset.knuthplass.paragraph;

import de.be.thaw.core.document.node.DocumentNode;

import java.util.function.IntToDoubleFunction;

public abstract class AbstractParagraph implements Paragraph {

    /**
     * Width of the paragraphs lines.
     */
    private final double lineWidth;

    /**
     * Document node representing the paragraph in the underlying document.
     */
    private final DocumentNode node;

    /**
     * A special line width supplier.
     */
    private IntToDoubleFunction lineWidthSupplier;

    public AbstractParagraph(double lineWidth, DocumentNode node) {
        this.lineWidth = lineWidth;
        this.node = node;
    }

    @Override
    public boolean isFloating() {
        return false; // Default floating behavior of paragraphs that do not support floating
    }

    /**
     * Set a special line width supplier.
     *
     * @param lineWidthSupplier to set
     */
    public void setLineWidthSupplier(IntToDoubleFunction lineWidthSupplier) {
        this.lineWidthSupplier = lineWidthSupplier;
    }

    public IntToDoubleFunction getLineWidthSupplier() {
        return lineWidthSupplier;
    }

    public double getDefaultLineWidth() {
        return lineWidth;
    }

    @Override
    public double getLineWidth(int lineNumber) {
        if (lineWidthSupplier != null) {
            return lineWidthSupplier.applyAsDouble(lineNumber);
        }

        return lineWidth;
    }

    @Override
    public DocumentNode getNode() {
        return node;
    }

}
