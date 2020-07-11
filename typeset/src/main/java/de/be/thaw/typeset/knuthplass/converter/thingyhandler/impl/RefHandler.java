package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.Optional;
import java.util.Set;

/**
 * Handler for hyper reference thingies.
 */
public class RefHandler implements ThingyHandler {

    private static final HeadlineHandler headlineHandler = new HeadlineHandler();

    @Override
    public Set<String> getThingyNames() {
        return Set.of("REF");
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

        // Check if target is a headline -> then we use headline numbering as display name of the reference
        DocumentNode targetNode = ctx.getDocument().getNodeForId(reference.getTargetID()).orElseThrow();
        ThingyNode thingyNode = (ThingyNode) targetNode.getTextNode();

        String displayString;
        if (isHeadlineThingyNode(thingyNode)) {
            displayString = thingyNode.getOptions().get("_numbering");
        } else {
            int refNum = ctx.getAndIncrementInternalRefCounter(reference.getCounterName());

            Optional<String> optionalPrefix = reference.getPrefix();
            if (optionalPrefix.isPresent()) {
                displayString = String.format("%s %d", optionalPrefix.get(), refNum);
            } else {
                displayString = String.valueOf(refNum);
            }
        }

        ctx.appendTextToParagraph(
                paragraph,
                displayString,
                documentNode
        );
    }

    /**
     * Check whether the passed thingy node is a headline thingy.
     *
     * @param node to check
     * @return whether headline node
     */
    private boolean isHeadlineThingyNode(ThingyNode node) {
        return headlineHandler.getThingyNames().contains(node.getName().toUpperCase());
    }

}
