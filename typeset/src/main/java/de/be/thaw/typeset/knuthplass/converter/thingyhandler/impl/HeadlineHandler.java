package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.Set;

/**
 * Handler dealing with headline thingies.
 */
public class HeadlineHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("H1", "H2", "H3", "H4", "H5", "H6");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        Paragraph paragraph = ctx.getCurrentParagraph();
        if (!(paragraph instanceof TextParagraph)) {
            throw new DocumentConversionException("Expected to be in a text paragraph");
        }

        String numberingString = node.getOptions().get("_numbering");
        if (numberingString == null) {
            numberingString = "";
        }

        ctx.appendWordToParagraph(
                (TextParagraph) paragraph,
                numberingString,
                documentNode
        );
    }

}
