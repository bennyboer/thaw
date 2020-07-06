package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.text.model.tree.impl.EnumerationItemNode;

/**
 * Box indicating an enumeration item start.
 */
public class EnumerationItemStartBox extends TextBox {

    /**
     * Indent of the enumeration item.
     */
    private final double indent;

    public EnumerationItemStartBox(String text, double width, EnumerationItemNode node, double indent) {
        super(text, width, node);

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
