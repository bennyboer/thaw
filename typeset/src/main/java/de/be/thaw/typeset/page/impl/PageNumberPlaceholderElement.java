package de.be.thaw.typeset.page.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

public class PageNumberPlaceholderElement extends TextElement {
    public PageNumberPlaceholderElement(double fontSize, DocumentNode node, int pageNumber, Size size, Position position) {
        super("", fontSize, new double[0], node, pageNumber, size, position);
    }
}
