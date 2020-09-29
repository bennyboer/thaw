package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.CiteHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.FootNoteHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.HyperRefHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.IncludeHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.RefHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.ReferenceListHandler;
import de.be.thaw.core.document.builder.impl.thingy.impl.VariableHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.util.PageRange;
import de.be.thaw.info.ThawInfo;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.reference.citation.CitationManager;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.selector.StyleSelector;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.value.BooleanStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.text.parser.exception.ParseException;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        initThingyHandler(new FootNoteHandler());
        initThingyHandler(new CiteHandler());
        initThingyHandler(new ReferenceListHandler());
        initThingyHandler(new VariableHandler());
    }

    /**
     * List of potential references.
     */
    private final List<PotentialInternalReference> potentialReferences = new ArrayList<>();

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

    /**
     * Mapping of all loaded header nodes.
     */
    private final Map<PageRange, DocumentNode> headerNodes = new HashMap<>();

    /**
     * Mapping of all loaded footer nodes.
     */
    private final Map<PageRange, DocumentNode> footerNodes = new HashMap<>();

    /**
     * List of foot notes mapped by document node IDs.
     */
    private final Map<String, DocumentNode> footNotes = new HashMap<>();

    /**
     * Parent document that is specified when typesetting nested documents.
     */
    @Nullable
    private Document parentDocument;

    public DocumentBuildContext(
            ThawInfo info,
            TextModel textModel,
            ReferenceModel referenceModel,
            StyleModel styleModel
    ) {
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

    public Map<PageRange, DocumentNode> getHeaderNodes() {
        return headerNodes;
    }

    public Map<PageRange, DocumentNode> getFooterNodes() {
        return footerNodes;
    }

    public Map<String, DocumentNode> getFootNotes() {
        return footNotes;
    }

    public CitationManager getCitationManager() {
        return getReferenceModel().getCitationManager();
    }

    /**
     * Process a box node that represents a paragraph.
     *
     * @param node   to process
     * @param parent the parent document node
     */
    public void processBoxNode(BoxNode node, DocumentNode parent) throws DocumentBuildException {
        processBoxNode(node, parent, null);
    }

    /**
     * Process a box node that represents a paragraph.
     *
     * @param node   to process
     * @param parent the parent document node
     * @param styles the styles to use
     */
    public void processBoxNode(BoxNode node, DocumentNode parent, @Nullable Styles styles) throws DocumentBuildException {
        // Find child node that may be a thingy node -> in that case we can apply special styles from the style model
        Optional<ThingyNode> optionalThingyNode = getFirstThingyNodeInBox(node);
        String blockName = null;
        String className = null;
        if (optionalThingyNode.isPresent()) {
            blockName = optionalThingyNode.get().getName();
            className = optionalThingyNode.get().getOptions().get("class");

            if (isHeadlineThingyName(blockName)) {
                ThingyNode thingyNode = optionalThingyNode.get();
                boolean isNumbered = Boolean.parseBoolean(thingyNode.getOptions().getOrDefault("numbered", "true"));

                if (isNumbered) {
                    int level = Integer.parseInt(String.valueOf(blockName.charAt(1)));

                    // Adjust the counter array size to fit the current level
                    if (getHeadlineCounter().size() < level) {
                        while (getHeadlineCounter().size() < level) {
                            getHeadlineCounter().add(0);
                        }
                    } else if (getHeadlineCounter().size() > level) {
                        while (getHeadlineCounter().size() > level) {
                            getHeadlineCounter().remove(getHeadlineCounter().size() - 1);
                        }
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

        if (styles == null) {
            // Get styles for the paragraph from the style model.
            List<StyleSelector> selectors = new ArrayList<>();
            if (blockName != null && !blockName.equalsIgnoreCase("paragraph")) {
                selectors.add(new StyleSelectorBuilder()
                        .setTargetName(blockName)
                        .setClassName(className)
                        .build());

                if (isHeadlineThingyName(blockName)) {
                    selectors.add(new StyleSelectorBuilder()
                            .setTargetName("h")
                            .setClassName(className)
                            .build());
                }
            }

            selectors.add(new StyleSelectorBuilder()
                    .setTargetName("paragraph")
                    .setClassName(className)
                    .build());
            selectors.add(new StyleSelectorBuilder()
                    .setTargetName("page")
                    .setClassName(className)
                    .build());

            styles = getStyleModel().select(selectors.toArray(StyleSelector[]::new));
        }

        DocumentNode documentNode = new DocumentNode(node, parent, styles);

        if (node.hasChildren()) {
            for (Node child : node.children()) {
                processNode(child, documentNode);
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
     * @param node   to process
     * @param parent the parent document node
     */
    private void processNode(Node node, DocumentNode parent) throws DocumentBuildException {
        Styles styles = new Styles(parent.getStyles());
        if (node.getType() == NodeType.FORMATTED) {
            FormattedNode formattedNode = (FormattedNode) node;

            if (formattedNode.getEmphases().contains(TextEmphasis.CODE)) { // Check if it is a monospaced node which needs special hyphenation handling
                styles.overrideStyle(StyleType.HYPHENATION, new BooleanStyleValue(false));
            } else if (formattedNode.getEmphases().contains(TextEmphasis.CUSTOM)) {
                // Try to load custom class styles
                getStyleModel().getBlock(new StyleSelectorBuilder()
                        .setTargetName("style")
                        .setClassName(formattedNode.getClassName().orElse(null))
                        .build()).ifPresent(styleBlock -> {
                    for (Map.Entry<StyleType, StyleValue> entry : styleBlock.getStyles().entrySet()) {
                        styles.overrideStyle(entry.getKey(), entry.getValue());
                    }
                });
            }
        }

        DocumentNode documentNode = new DocumentNode(
                node,
                parent,
                styles
        );

        if (node.hasChildren()) {
            for (Node child : node.children()) {
                processNode(child, documentNode);
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
            getReferenceModel().addLabel(label, documentNode.getId());
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
     * Load the header or footer node from the passed folder.
     *
     * @param folder to load from
     * @return the loaded node
     * @throws DocumentBuildException in case the node could not be loaded
     */
    public DocumentNode loadHeaderFooterNode(File folder) throws DocumentBuildException {
        if (!folder.exists()) {
            throw new DocumentBuildException(String.format(
                    "Expected the header or footer folder '%s' that has been specified to exist",
                    folder.getAbsolutePath()
            ));
        }

        // Get text file
        String[] textFiles = folder.list((dir, name) -> name.endsWith(".tdt"));
        if (textFiles.length == 0) {
            throw new DocumentBuildException(String.format(
                    "Could not find a text file (ending with *.tdt) in the header or footer folder specified at '%s'",
                    folder.getAbsolutePath()
            ));
        } else if (textFiles.length > 1) {
            throw new DocumentBuildException(String.format(
                    "Could find more than one text file (ending with *.tdt) in the header or footer folder specified at '%s'",
                    folder.getAbsolutePath()
            ));
        }
        File textFile = new File(folder, textFiles[0]);

        TextModel textModel;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), ThawContext.getInstance().getEncoding()))) {
            textModel = ThawContext.getInstance().getTextParser().parse(br);
        } catch (IOException | ParseException e) {
            throw new DocumentBuildException(String.format(
                    "Could not parse text file at '%s' that should be used as header or footer",
                    textFile.getAbsolutePath()
            ), e);
        }

        // Get style model
        StyleModel styleModel = getStyleModel();
        String[] styleFiles = folder.list((dir, name) -> name.endsWith(".tds"));
        if (styleFiles.length > 1) {
            throw new DocumentBuildException(String.format(
                    "Could find more than one style file (ending with *.tds) in the folder specified as header or footer at '%s'",
                    folder.getAbsolutePath()
            ));
        } else if (styleFiles.length == 1) {
            File styleFile = new File(folder, styleFiles[0]);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(styleFile), ThawContext.getInstance().getEncoding()))) {
                styleModel = ThawContext.getInstance().getStyleParser().parse(br, folder);
            } catch (IOException | StyleModelParseException e) {
                throw new DocumentBuildException(String.format(
                        "Could not parse style file at '%s' that should be included as header or footer",
                        styleFile.getAbsolutePath()
                ));
            }

            // Merge current style model and the new one
            styleModel = styleModel.merge(getStyleModel());
        }

        DocumentNode root = new DocumentNode(
                textModel.getRoot(),
                null,
                styleModel.select(new StyleSelectorBuilder().build())
        );

        File oldProcessingFolder = ThawContext.getInstance().getCurrentFolder();
        ThawContext.getInstance().setCurrentFolder(folder); // Set the currently processing folder
        StyleModel oldStyleModel = getStyleModel();
        setStyleModel(styleModel); // Set the new style model

        for (Node node : textModel.getRoot().children()) {
            if (node.getType() == NodeType.BOX) {
                processBoxNode((BoxNode) node, root);
            }
        }

        ThawContext.getInstance().setCurrentFolder(oldProcessingFolder); // Reset the currently processing folder
        setStyleModel(oldStyleModel); // Reset to the old style model

        return root;
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

    public Optional<Document> getParentDocument() {
        return Optional.ofNullable(parentDocument);
    }

    public void setParentDocument(@Nullable Document parentDocument) {
        this.parentDocument = parentDocument;
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
         * Whether the internal reference comes from a citation and points to a reference list entry.
         */
        private final boolean fromCitation;

        public PotentialInternalReference(String sourceID, String targetLabel, String prefix) {
            this(sourceID, targetLabel, prefix, false);
        }

        public PotentialInternalReference(
                String sourceID,
                String targetLabel,
                String prefix,
                boolean fromCitation
        ) {
            this.sourceID = sourceID;
            this.targetLabel = targetLabel;
            this.prefix = prefix;
            this.fromCitation = fromCitation;
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

        /**
         * Check whether the internal reference comes from a citation and points to a reference list entry.
         *
         * @return whether reference originates from a citation thingy
         */
        public boolean isFromCitation() {
            return fromCitation;
        }

    }

}
