package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.item.impl.box.PageNumberPlaceholderBox;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.Set;

/**
 * Handler for dealing with page thingies.
 */
public class PageHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("PAGE");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        if (!(ctx.getCurrentParagraph() instanceof TextParagraph)) {
            throw new DocumentConversionException(String.format(
                    "Expected the #REF# Thingy to be inside a text paragraph at %s",
                    node.getTextPosition()
            ));
        }

        TextParagraph paragraph = (TextParagraph) ctx.getCurrentParagraph();
        paragraph.addItem(new PageNumberPlaceholderBox(documentNode));
    }

}
