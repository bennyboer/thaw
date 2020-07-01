package de.be.thaw.typeset.page.impl;

import de.be.thaw.core.typesetting.page.element.AbstractElement;
import de.be.thaw.core.util.Position;
import de.be.thaw.core.util.Size;
import de.be.thaw.text.model.tree.Node;

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
     * The original node the text belongs to in the thaw document.
     * Can be used to derive the used font, style, etc.
     */
    private final Node node;

    public TextElement(String text, Node node, Size size, Position position) {
        super(size, position);

        this.text = text;
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

    /**
     * Get the original node the text belongs to in the thaw document.
     *
     * @return node
     */
    public Node getNode() {
        return node;
    }

}
