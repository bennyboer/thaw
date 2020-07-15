package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.HyperRefHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.IncludeHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.RefHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.info.ThawInfo;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Context used during building of the document.
 */
public class DocumentBuildContext {

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
     * List of potential references.
     */
    private final List<PotentialInternalReference> potentialReferences = new ArrayList<>();

    /**
     * Lookup from labels to their node ID.
     */
    private final Map<String, String> labelToNodeID = new HashMap<>();

    /**
     * Counter for headline numbering.
     */
    private final List<Integer> headlineCounter = new ArrayList<>();

    /**
     * The document info.
     */
    private final ThawInfo info;

    /**
     * Text model of the document.
     */
    private final TextModel textModel;

    /**
     * Reference model of the document.
     */
    private final ReferenceModel referenceModel;

    /**
     * Style model of the document.
     */
    private StyleModel styleModel;

    public DocumentBuildContext(ThawInfo info, TextModel textModel, ReferenceModel referenceModel, StyleModel styleModel) {
        this.info = info;
        this.textModel = textModel;
        this.referenceModel = referenceModel;
        this.styleModel = styleModel;
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

    public List<PotentialInternalReference> getPotentialReferences() {
        return potentialReferences;
    }

    public Map<String, String> getLabelToNodeID() {
        return labelToNodeID;
    }

    public List<Integer> getHeadlineCounter() {
        return headlineCounter;
    }

    public ThawInfo getInfo() {
        return info;
    }

    public TextModel getTextModel() {
        return textModel;
    }

    public ReferenceModel getReferenceModel() {
        return referenceModel;
    }

    public StyleModel getStyleModel() {
        return styleModel;
    }

    public void setStyleModel(StyleModel styleModel) {
        this.styleModel = styleModel;
    }

    /**
     * Process a box node that represents a paragraph.
     *
     * @param node   to process
     * @param parent the parent document node
     */
    public void processBoxNode(BoxNode node, DocumentNode parent, DocumentNodeStyle parentStyleNode) throws DocumentBuildException {
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
                    while (getHeadlineCounter().size() < level) {
                        getHeadlineCounter().add(0);
                    }

                    // Increase counter for the level
                    getHeadlineCounter().set(level - 1, getHeadlineCounter().get(level - 1) + 1);

                    // Save numbering string for later
                    String numberingString = getHeadlineCounter().stream().map(String::valueOf).collect(Collectors.joining("."));
                    optionalThingyNode.get().getOptions().put("_numbering", numberingString);

                    // Retrieve and store the headline text for later
                    String headlineText = getTextContent(node);
                    optionalThingyNode.get().getOptions().put("_headline", headlineText);
                }
            }
        }

        StyleBlock styleBlock = getStyleBlock("PARAGRAPH", blockName, getStyleModel());

        DocumentNodeStyle styleNode = new DocumentNodeStyle(parentStyleNode, styleBlock.getStyles());

        DocumentNode documentNode = new DocumentNode(node, parent, styleNode);
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                processNode(child, documentNode, styleNode);
            }
        }
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
     * @param parent          the parent document node
     * @param parentStyleNode the parent style node
     */
    private void processNode(Node node, DocumentNode parent, DocumentNodeStyle parentStyleNode) throws DocumentBuildException {
        DocumentNodeStyle style = new DocumentNodeStyle(parentStyleNode, null); // Empty node style TODO Allow styling every node?

        DocumentNode documentNode = new DocumentNode(node, parent, style);

        if (node.hasChildren()) {
            for (Node child : node.children()) {
                processNode(child, documentNode, style);
            }
        }

        if (node.getType() == NodeType.THINGY) {
            processThingy((ThingyNode) node, documentNode);
        }
    }

    /**
     * Process a thingy.
     *
     * @param thingyNode   to process
     * @param documentNode the document node the thingy node is contained in
     */
    private void processThingy(ThingyNode thingyNode, DocumentNode documentNode) throws DocumentBuildException {
        String label = thingyNode.getOptions().get("label");
        if (label != null) {
            // Add as potential target
            getLabelToNodeID().put(label, documentNode.getId());
        }

        Optional<ThingyHandler> optionalThingyHandler = getThingyHandler(thingyNode.getName());
        if (optionalThingyHandler.isPresent()) {
            optionalThingyHandler.get().handle(thingyNode, documentNode, this);
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

    /**
     * Check whether the passed counter name is a headline counter.
     *
     * @param counterName to check
     * @return whether headline counter name
     */
    public boolean isHeadlineThingyName(String counterName) {
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
     * A potential internal reference.
     */
    public static class PotentialInternalReference {

        /**
         * ID of the source node.
         */
        private final String sourceID;

        /**
         * Label of the target.
         */
        private final String targetLabel;

        /**
         * Prefix to prefix the reference number with.
         */
        private final String prefix;

        /**
         * Name of the counter to use.
         */
        private final String counterName;

        public PotentialInternalReference(String sourceID, String targetLabel, String prefix, String counterName) {
            this.sourceID = sourceID;
            this.targetLabel = targetLabel;
            this.prefix = prefix;
            this.counterName = counterName;
        }

        public String getSourceID() {
            return sourceID;
        }

        public String getTargetLabel() {
            return targetLabel;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getCounterName() {
            return counterName;
        }

    }

}
