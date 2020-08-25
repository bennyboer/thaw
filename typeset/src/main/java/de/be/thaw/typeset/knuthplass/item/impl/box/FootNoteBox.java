package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;

/**
 * Box representing a foot note.
 */
public class FootNoteBox extends TextBox {

    public FootNoteBox(String text, FontDetailsSupplier.StringMetrics metrics, DocumentNode node) {
        super(text, metrics, node);
    }

}
