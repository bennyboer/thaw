package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.Optional;

/**
 * Handler for hyper reference thingies.
 */
public class RefHandler implements ThingyHandler {

    @Override
    public String getThingyName() {
        return "REF";
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

        // Fetch reference from the documents reference model
        InternalReference reference = (InternalReference) ctx.getDocument().getReferenceModel().getReference(documentNode.getId()).orElseThrow();

        int refNum = ctx.getAndIncrementInternalRefCounter(reference.getCounterName());

        String str;
        Optional<String> optionalPrefix = reference.getPrefix();
        if (optionalPrefix.isPresent()) {
            str = String.format("%s %d", optionalPrefix.get(), refNum);
        } else {
            str = String.valueOf(refNum);
        }

        ctx.appendWordToParagraph(
                paragraph,
                str,
                documentNode
        );
    }

}
