package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;

/**
 * Box indicating an enumeration item start.
 */
public class EnumerationItemStartBox extends TextBox {

    /**
     * Indent of the enumeration item.
     */
    private final double indent;

    public EnumerationItemStartBox(String text, FontDetailsSupplier.StringMetrics metrics, DocumentNode node, double indent) {
        super(text, metrics, node);

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
