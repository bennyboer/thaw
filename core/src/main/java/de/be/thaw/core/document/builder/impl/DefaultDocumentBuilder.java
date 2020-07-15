package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.DocumentBuilder;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.HyperRefHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.IncludeHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.RefHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.reference.impl.DefaultReferenceModel;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.RootNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Builder building a document from the provided source.
 */
public class DefaultDocumentBuilder implements DocumentBuilder<DocumentBuildSource> {

    /**
     * Mapping of thingy names to their handler.
     */
    private static final Map<String, ThingyHandler> THINGY_HANDLER_MAP = new HashMap<>();

    static {
        initThingyHandler(new RefHandler());
        initThingyHandler(new HyperRefHandler());
        initThingyHandler(new IncludeHandler());
    }

    /**
     * Initialize the passed thingy handler.
     *
     * @param handler to initialize
     */
    private static void initThingyHandler(ThingyHandler handler) {
        for (String name : handler.getThingyNames()) {
            THINGY_HANDLER_MAP.put(name.toLowerCase(), handler);
        }
    }

    /**
     * Get a thingy handler by the passed thingy name.
     *
     * @param name to get handler for
     * @return the thingy handler or an empty optional
     */
    private static Optional<ThingyHandler> getThingyHandler(String name) {
        return Optional.ofNullable(THINGY_HANDLER_MAP.get(name.toLowerCase()));
    }

    @Override
    public Document build(DocumentBuildSource source) throws DocumentBuildException {
        ReferenceModel referenceModel = new DefaultReferenceModel();
        DocumentBuildContext ctx = new DocumentBuildContext(source.getInfo(), source.getTextModel(), referenceModel, source.getStyleModel());

        Document document = new Document(source.getInfo(), toRootNode(ctx), referenceModel);

        processPotentialReferences(document, ctx);

        return document;
    }

    /**
     * Process all dangling potential references.
     *
     * @param document of the references
     * @param ctx      the build context
     * @throws MissingReferenceTargetException in case a references target is not found
     */
    private void processPotentialReferences(Document document, DocumentBuildContext ctx) throws MissingReferenceTargetException {
        // Add potential targets to reference model if the target has been found, otherwise throw an exception
        for (DocumentBuildContext.PotentialInternalReference potentialReference : ctx.getPotentialReferences()) {
            String targetID = ctx.getLabelToNodeID().get(potentialReference.getTargetLabel());
            if (targetID == null) {
                throw new MissingReferenceTargetException(String.format(
                        "Reference target with label '%s' is missing",
                        potentialReference.getTargetLabel()
                ));
            }

            String counterName = potentialReference.getCounterName();
            if (counterName == null) {
                DocumentNode targetNode = document.getNodeForId(targetID).orElseThrow();
                ThingyNode thingyNode = (ThingyNode) targetNode.getTextNode();
                counterName = thingyNode.getName();
            }

            ctx.getReferenceModel().addReference(new InternalReference(
                    potentialReference.getSourceID(),
                    targetID,
                    counterName.toLowerCase(),
                    potentialReference.getPrefix()
            ));
        }
    }

    /**
     * Check whether the passed counter name is a headline counter.
     *
     * @param counterName to check
     * @return whether headline counter name
     */
    private boolean isHeadlineThingyName(String counterName) {
        if (counterName.length() != 2) {
            return false;
        }

        if (counterName.charAt(0) != 'H' && counterName.charAt(0) != 'h') {
            return false;
        }

        char num = counterName.charAt(1);
        try {
            Integer.parseInt(String.valueOf(num));
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * Convert the passed models to a document node.
     *
     * @param ctx the document build context
     * @return the root document node
     */
    private DocumentNode toRootNode(DocumentBuildContext ctx) throws DocumentBuildException {
        return processRootNode(
                ctx.getTextModel().getRoot(),
                new DocumentNodeStyle(null, ctx.getStyleModel().getBlock("DOCUMENT").orElseThrow().getStyles()),
                ctx
        );
    }

    /**
     * Convert to a root node.
     *
     * @param node            to convert
     * @param parentStyleNode the parent style node
     * @param ctx             the build context
     * @return the root document node
     */
    private DocumentNode processRootNode(RootNode node, DocumentNodeStyle parentStyleNode, DocumentBuildContext ctx) throws DocumentBuildException {
        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processBoxNode((BoxNode) child, parentStyleNode, ctx));
            }
        }

        return new DocumentNode(node, parentStyleNode, children);
    }

    /**
     * Process a box node that represents a paragraph.
     *
     * @param node            to process
     * @param parentStyleNode the style node of the parent
     * @param ctx             the build context
     * @return the box node as a document node
     */
    private DocumentNode processBoxNode(BoxNode node, DocumentNodeStyle parentStyleNode, DocumentBuildContext ctx) throws DocumentBuildException {
        // Find child node that may be a thingy node -> in that case we can apply special styles from the style model
        Optional<ThingyNode> optionalThingyNode = getFirstThingyNodeInBox(node);
        String blockName = null;
        if (optionalThingyNode.isPresent()) {
            blockName = optionalThingyNode.get().getName();

            if (isHeadlineThingyName(blockName)) {
                ThingyNode thingyNode = optionalThingyNode.get();
                boolean isNumbered = Boolean.parseBoolean(thingyNode.getOptions().getOrDefault("numbered", "true"));

                if (isNumbered) {
                    int level = Integer.parseInt(String.valueOf(blockName.charAt(1)));

                    // Check if counter size is great enough
                    while (ctx.getHeadlineCounter().size() < level) {
                        ctx.getHeadlineCounter().add(0);
                    }

                    // Increase counter for the level
                    ctx.getHeadlineCounter().set(level - 1, ctx.getHeadlineCounter().get(level - 1) + 1);

                    // Save numbering string for later
                    String numberingString = ctx.getHeadlineCounter().stream().map(String::valueOf).collect(Collectors.joining("."));
                    optionalThingyNode.get().getOptions().put("_numbering", numberingString);

                    // Retrieve and store the headline text for later
                    String headlineText = getTextContent(node);
                    optionalThingyNode.get().getOptions().put("_headline", headlineText);
                }
            }
        }

        StyleBlock styleBlock = getStyleBlock("PARAGRAPH", blockName, ctx.getStyleModel());

        DocumentNodeStyle styleNode = new DocumentNodeStyle(parentStyleNode, styleBlock.getStyles());

        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processNode(child, styleNode, ctx));
            }
        }

        return new DocumentNode(node, styleNode, children);
    }

    /**
     * Get only the text content of the passed node.
     *
     * @param node to get text content of
     * @return text content
     */
    private String getTextContent(Node node) {
        StringBuilder buffer = new StringBuilder();

        fillTextContent(buffer, node);

        return buffer.toString();
    }

    /**
     * Fill the buffer with the text content of the passed node.
     *
     * @param buffer to fill
     * @param node   node to fill buffer for
     */
    private void fillTextContent(StringBuilder buffer, Node node) {
        if (node.getType() == NodeType.TEXT) {
            TextNode tn = (TextNode) node;
            buffer.append(tn.getValue());
        } else if (node.getType() == NodeType.FORMATTED) {
            FormattedNode fn = (FormattedNode) node;
            buffer.append(fn.getValue());
        }

        if (node.hasChildren()) {
            for (Node child : node.children()) {
                fillTextContent(buffer, child);
            }
        }
    }

    /**
     * Process another node.
     *
     * @param node            to process
     * @param parentStyleNode the parent style node
     * @param ctx             the build context
     * @return a document node
     */
    private DocumentNode processNode(Node node, DocumentNodeStyle parentStyleNode, DocumentBuildContext ctx) throws DocumentBuildException {
        DocumentNodeStyle style = new DocumentNodeStyle(parentStyleNode, null); // Empty node style TODO Allow styling every node?

        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processNode(child, style, ctx));
            }
        }

        DocumentNode documentNode = new DocumentNode(node, style, children);

        if (node.getType() == NodeType.THINGY) {
            processThingy((ThingyNode) node, documentNode, ctx);
        }

        return documentNode;
    }

    /**
     * Process a thingy.
     *
     * @param thingyNode   to process
     * @param documentNode the document node the thingy node is contained in
     * @param ctx          the build context
     */
    private void processThingy(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        String label = thingyNode.getOptions().get("label");
        if (label != null) {
            // Add as potential target
            ctx.getLabelToNodeID().put(label, documentNode.getId());
        }

        Optional<ThingyHandler> optionalThingyHandler = getThingyHandler(thingyNode.getName());
        if (optionalThingyHandler.isPresent()) {
            optionalThingyHandler.get().handle(thingyNode, documentNode, ctx);
        }
    }

    /**
     * Get the first thingy node in the passed box or null
     * if the first node is not a thingy.
     *
     * @param node to get first thingy node in
     * @return thingy node
     */
    private Optional<ThingyNode> getFirstThingyNodeInBox(BoxNode node) {
        if (node.hasChildren()) {
            Node first = node.children().get(0);
            if (first.getType() == NodeType.THINGY) {
                return Optional.of((ThingyNode) first);
            }
        }

        return Optional.empty();
    }

    /**
     * Get a style block.
     *
     * @param defaultBlockName the name of the default styles block name
     * @param blockName        the specific block name (if any)
     * @return style block
     */
    private StyleBlock getStyleBlock(String defaultBlockName, String blockName, StyleModel styleModel) {
        Map<StyleType, Style> styles = new HashMap<>();

        // Apply default styles
        styleModel.getBlock(defaultBlockName).ifPresent(styleBlock -> {
            for (Map.Entry<StyleType, Style> styleEntry : styleBlock.getStyles().entrySet()) {
                styles.put(styleEntry.getKey(), styleEntry.getValue());
            }
        });

        styleModel.getBlock(blockName).ifPresent(styleBlock -> {
            for (Map.Entry<StyleType, Style> styleEntry : styleBlock.getStyles().entrySet()) {
                Style style = styles.get(styleEntry.getKey());
                if (style != null) {
                    styles.put(styleEntry.getKey(), styleEntry.getValue().merge(style));
                } else {
                    styles.put(styleEntry.getKey(), styleEntry.getValue());
                }
            }
        });

        return new StyleBlock(blockName, styles);
    }

}
