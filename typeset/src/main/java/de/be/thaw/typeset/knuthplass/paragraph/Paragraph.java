package de.be.thaw.typeset.knuthplass.paragraph;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;

/**
 * A paragraph representation to process using the Knuth-Plass line breaking algorithm.
 */
public class Paragraph {

    /**
     * The items the paragraph consists of (boxes, glues and penalties).
     */
    private final List<Item> items = new ArrayList<>();

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

    public Paragraph(double lineWidth, DocumentNode node) {
        this.lineWidth = lineWidth;
        this.node = node;
    }

    /**
     * Add an item to the paragraph.
     *
     * @param item to add
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Get the items of the paragraph.
     *
     * @return items
     */
    public List<Item> items() {
        return items;
    }

    /**
     * Check whether the paragraph does not have items yet.
     *
     * @return whether empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Set a special line width supplier.
     *
     * @param lineWidthSupplier to set
     */
    public void setLineWidthSupplier(IntToDoubleFunction lineWidthSupplier) {
        this.lineWidthSupplier = lineWidthSupplier;
    }

    /**
     * Get the line width for the passed line number.
     *
     * @param lineNumber to get line width for
     * @return line width
     */
    public double getLineWidth(int lineNumber) {
        if (lineWidthSupplier != null) {
            return lineWidthSupplier.applyAsDouble(lineNumber);
        }

        return lineWidth;
    }

    /**
     * Node representing the paragraph in the underlying document.
     *
     * @return node
     */
    public DocumentNode getNode() {
        return node;
    }

}
