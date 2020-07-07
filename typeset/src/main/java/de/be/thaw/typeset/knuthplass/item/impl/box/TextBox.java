package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.Node;
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
     * The original node representing this text box in the thaw document model.
     */
    private final DocumentNode node;

    public TextBox(String text, double width, DocumentNode node) {
        this.text = text;
        this.width = width;
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

}
