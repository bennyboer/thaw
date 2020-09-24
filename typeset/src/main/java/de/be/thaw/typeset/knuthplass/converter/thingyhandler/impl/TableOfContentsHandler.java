package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.toc.TableOfContentsItemParagraph;
import de.be.thaw.util.unit.Unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Handler for the table of contents thingy.
 */
public class TableOfContentsHandler implements ThingyHandler {

    private static final HeadlineHandler headlineHandler = new HeadlineHandler();

    @Override
    public Set<String> getThingyNames() {
        return Set.of("TOC");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        List<DocumentNode> headlineThingyNodes = getHeadlineThingyNodes(ctx.getDocument());

        ctx.finalizeParagraph();

        double maxNumberWidth = getWidestNumberWidth(documentNode, ctx);
        double maxPageNumberWidth = 5 * maxNumberWidth; // Max supported page number is 99999!

        for (DocumentNode n : headlineThingyNodes) {
            ThingyNode headlineThingyNode = (ThingyNode) n.getTextNode();
            boolean isNumbered = Boolean.parseBoolean(headlineThingyNode.getOptions().getOrDefault("numbered", "true"));

            if (isNumbered) {
                String numbering = headlineThingyNode.getOptions().get("_numbering");
                String headline = headlineThingyNode.getOptions().get("_headline");
                int level = Integer.parseInt(String.valueOf(headlineThingyNode.getName().charAt(1)));

                // Fetch styles for that particular level (if any)
                Styles tocEntryLevelStyles = ctx.getDocument()
                        .getStyleModel()
                        .select(new StyleSelectorBuilder()
                                        .setTargetName("toc-entry")
                                        .setPseudoClassName("level")
                                        .setPseudoClassSettings(Collections.singletonList(String.format("%d", level)))
                                        .build(),
                                new StyleSelectorBuilder()
                                        .setTargetName("toc")
                                        .build());

                StyleValue marginLeftValue = tocEntryLevelStyles.resolve(StyleType.MARGIN_LEFT).orElseThrow();
                final double marginLeft = Unit.convert(marginLeftValue.doubleValue(), marginLeftValue.unit().orElse(Unit.MILLIMETER), Unit.POINTS);

                // Calculate numbering string metrics
                FontDetailsSupplier.StringMetrics numberingMetrics;
                try {
                    numberingMetrics = ctx.getConfig().getFontDetailsSupplier().measureString(documentNode, -1, String.format("%s ", numbering));
                } catch (Exception e) {
                    throw new DocumentConversionException(e);
                }

                double lineWidth = ctx.getLineWidth();

                // Create dummy document nodes
                Styles numberingStyles = new Styles(tocEntryLevelStyles);
                numberingStyles.overrideStyle(StyleType.MARGIN_LEFT, new DoubleStyleValue(marginLeft + numberingMetrics.getWidth(), Unit.POINTS));
                DocumentNode dummyNumberingNode = new DocumentNode(String.format("TOC_%s", numbering), documentNode.getTextNode(), null, numberingStyles);

                DocumentNode dummyHeadlineNode = new DocumentNode(String.format("TOC_%s", headline), documentNode.getTextNode(), null, numberingStyles);

                ctx.getDocument().getReferenceModel().addReference(new InternalReference(dummyNumberingNode.getId(), n.getId(), "TOC_numbering"));
                ctx.getDocument().getReferenceModel().addReference(new InternalReference(dummyHeadlineNode.getId(), n.getId(), "TOC_headline"));

                TableOfContentsItemParagraph paragraph = new TableOfContentsItemParagraph(
                        lineWidth - maxPageNumberWidth,
                        dummyNumberingNode,
                        maxPageNumberWidth,
                        numbering
                );
                ctx.setCurrentParagraph(paragraph);

                // Add headline text -> numbering will be added later
                ctx.appendTextToParagraph(paragraph, headline, dummyHeadlineNode);
                ctx.finalizeParagraph();
            }
        }
    }

    /**
     * Get the widest number width
     *
     * @param node to get widest number width for
     * @param ctx  the conversion context
     * @return the widest number width
     * @throws DocumentConversionException in case the width of the numbers could not be determined
     */
    private double getWidestNumberWidth(DocumentNode node, ConversionContext ctx) throws DocumentConversionException {
        double maxWidth = 0;
        for (int i = 0; i <= 9; i++) {
            try {
                double width = ctx.getConfig().getFontDetailsSupplier().measureString(node, -1, String.valueOf(i)).getWidth();
                if (width > maxWidth) {
                    maxWidth = width;
                }
            } catch (Exception e) {
                throw new DocumentConversionException(e);
            }
        }

        return maxWidth;
    }

    /**
     * Get all headline thingy nodes.
     *
     * @param document to get nodes from
     * @return headline thingy nodes
     */
    private List<DocumentNode> getHeadlineThingyNodes(Document document) {
        List<DocumentNode> result = new ArrayList<>();

        fillHeadlineThingyNodesList(result, document.getRoot());

        return result;
    }

    /**
     * Fill the passed headline thingy nodes list for the given node.
     *
     * @param headlineThingyNodes to fill
     * @param node                to fill for
     */
    private void fillHeadlineThingyNodesList(List<DocumentNode> headlineThingyNodes, DocumentNode node) {
        if (node.getTextNode().getType() == NodeType.THINGY && isHeadlineThingyNode((ThingyNode) node.getTextNode())) {
            headlineThingyNodes.add(node);
        }

        if (node.hasChildren()) {
            for (DocumentNode child : node.getChildren()) {
                fillHeadlineThingyNodesList(headlineThingyNodes, child);
            }
        }
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
