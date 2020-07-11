package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.Optional;
import java.util.Set;

/**
 * Handler dealing with explicit line or page breaks.
 */
public class ExplicitBreakHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("BREAK");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        boolean isPageBreak = Optional.ofNullable(node.getOptions().get("type"))
                .orElse("LINE")
                .equalsIgnoreCase("PAGE");

        if (isPageBreak) {
            onPageBreak(ctx);
        } else {
            onLineBreak(ctx);
        }
    }

    /**
     * Called on an explicit line break.
     *
     * @param ctx the conversion context
     */
    private void onLineBreak(ConversionContext ctx) throws DocumentConversionException {
        if (!(ctx.getCurrentParagraph() instanceof TextParagraph)) {
            throw new DocumentConversionException("Cannot line break here");
        }

        TextParagraph current = (TextParagraph) ctx.getCurrentParagraph();

        // Add glue as stretchable space to fill the last line
        current.addItem(new Glue(0, ctx.getConfig().getPageSize().getWidth(), 0));

        // Add explicit line break
        current.addItem(new Penalty(Double.NEGATIVE_INFINITY, 0, true));
    }

    /**
     * Called on an explicit page break.
     *
     * @param ctx the conversion context
     */
    private void onPageBreak(ConversionContext ctx) {
        Paragraph lastParagraph = ctx.getCurrentParagraph();

        ctx.finalizeParagraph(); // Finalize the current paragraph
        ctx.finalizeConsecutiveParagraphList(); // Finalize the list of consecutive paragraphs (because of page break!)

        if (lastParagraph instanceof TextParagraph) {
            TextParagraph textParagraph = (TextParagraph) lastParagraph;

            ctx.initializeNewTextParagraph(textParagraph.getNode());
        }
    }

}
