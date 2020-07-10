package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;

/**
 * Box indicating an enumeration item start.
 */
public class EnumerationItemStartBox extends TextBox {

    /**
     * Indent of the enumeration item.
     */
    private final double indent;

    public EnumerationItemStartBox(String text, double width, double fontSize, DocumentNode node, double indent) {
        super(text, width, 1, null, node);

        this.indent = indent;
    }

    /**
     * Get the indent of the enumeration item.
     *
     * @return indent
     */
    public double getIndent() {
        return indent;
    }

}
