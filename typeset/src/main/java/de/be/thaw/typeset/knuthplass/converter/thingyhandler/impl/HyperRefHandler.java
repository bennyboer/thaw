package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.reference.impl.ExternalReference;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.Set;

/**
 * Handler for hyper reference thingies.
 */
public class HyperRefHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("HREF");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        if (!(ctx.getCurrentParagraph() instanceof TextParagraph)) {
            throw new DocumentConversionException(String.format(
                    "Expected the #HREF# Thingy to be inside a text paragraph at %s",
                    node.getTextPosition()
            ));
        }

        TextParagraph paragraph = (TextParagraph) ctx.getCurrentParagraph();

        // Fetch reference from the documents reference model
        ExternalReference reference = (ExternalReference) ctx.getDocument().getReferenceModel().getReference(documentNode.getId()).orElseThrow();

        ctx.appendWordToParagraph(paragraph, reference.getDisplayName(), documentNode);
    }

}
