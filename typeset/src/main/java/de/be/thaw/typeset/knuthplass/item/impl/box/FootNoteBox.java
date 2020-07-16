package de.be.thaw.typeset.knuthplass.item.impl.box;

import de.be.thaw.core.document.node.DocumentNode;

/**
 * Box representing a foot note.
 */
public class FootNoteBox extends TextBox {

    public FootNoteBox(String text, double width, double fontSize, double[] kerningAdjustments, DocumentNode node) {
        super(text, width, fontSize, kerningAdjustments, node);
    }

}
