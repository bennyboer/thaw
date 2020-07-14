package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.item.impl.box.EmptyBox;
import de.be.thaw.typeset.knuthplass.paragraph.impl.toc.TableOfContentsItemParagraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

                double lineWidth = ctx.getLineWidth();
                double indentPerLevel = lineWidth / 20; // TODO Set in style
                double indent = (level - 1) * indentPerLevel;

                // Create dummy document node
                Map<StyleType, Style> styles = new HashMap<>();
                styles.put(StyleType.INSETS, new InsetsStyle(0.0, indent, 0.0, 0.0));
                DocumentNode dummy = new DocumentNode(String.format("TOC_%s", numbering), documentNode.getTextNode(), new DocumentNodeStyle(documentNode.getStyle(), styles), documentNode.getChildren());

                ctx.getDocument().getReferenceModel().addReference(new InternalReference(dummy.getId(), n.getId(), "TOC"));

                TableOfContentsItemParagraph paragraph = new TableOfContentsItemParagraph(lineWidth - maxPageNumberWidth, dummy, maxPageNumberWidth);
                paragraph.addItem(new EmptyBox(0));
                ctx.setCurrentParagraph(paragraph);

                // Add numbering and headline text
                ctx.appendTextToParagraph(paragraph, String.format("%s %s", numbering, headline), dummy);
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
