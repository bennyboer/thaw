package de.be.thaw.typeset.page.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

public class PageNumberPlaceholderElement extends TextElement {

    public PageNumberPlaceholderElement(FontDetailsSupplier.StringMetrics metrics, DocumentNode node, int pageNumber, Size size, Position position) {
        super("", metrics, node, pageNumber, size, position);
    }

}
