package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.DocumentBuilder;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.reference.impl.DefaultReferenceModel;
import de.be.thaw.reference.impl.ExternalReference;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.RootNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Builder building a document from the provided source.
 */
public class DefaultDocumentBuilder implements DocumentBuilder<DocumentBuildSource> {

    /**
     * List of potential references.
     */
    private final List<PotentialInternalReference> potentialReferences = new ArrayList<>();

    /**
     * Lookup from labels to their node ID.
     */
    private final Map<String, String> labelToNodeID = new HashMap<>();

    @Override
    public Document build(DocumentBuildSource source) throws DocumentBuildException {
        potentialReferences.clear();
        labelToNodeID.clear();

        ReferenceModel referenceModel = new DefaultReferenceModel();
        DocumentNode root = toRootNode(source.getTextModel(), source.getStyleModel(), referenceModel);

        // Add potential targets to reference model if the target has been found, otherwise throw an exception
        for (PotentialInternalReference potentialReference : potentialReferences) {
            String targetID = labelToNodeID.get(potentialReference.getTargetLabel());
            if (targetID == null) {
                throw new MissingReferenceTargetException(String.format(
                        "Reference target with label '%s' is missing",
                        potentialReference.getTargetLabel()
                ));
            }

            referenceModel.addReference(new InternalReference(
                    potentialReference.getSourceID(),
                    targetID,
                    potentialReference.getCounterName(),
                    potentialReference.getPrefix()
            ));
        }

        return new Document(source.getInfo(), root, referenceModel);
    }

    /**
     * Convert the passed models to a document node.
     *
     * @param textModel      to convert
     * @param styleModel     to convert
     * @param referenceModel the reference model
     * @return the root document node
     */
    private DocumentNode toRootNode(TextModel textModel, StyleModel styleModel, ReferenceModel referenceModel) throws DocumentBuildException {
        return processRootNode(
                textModel.getRoot(),
                new DocumentNodeStyle(null, styleModel.getBlock("DOCUMENT").orElseThrow().getStyles()),
                styleModel,
                referenceModel
        );
    }

    /**
     * Convert to a root node.
     *
     * @param node            to convert
     * @param parentStyleNode the parent style node
     * @param styleModel      to convert
     * @param referenceModel  the reference model
     * @return the root document node
     */
    private DocumentNode processRootNode(RootNode node, DocumentNodeStyle parentStyleNode, StyleModel styleModel, ReferenceModel referenceModel) throws DocumentBuildException {
        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processBoxNode((BoxNode) child, parentStyleNode, styleModel, referenceModel));
            }
        }

        return new DocumentNode(node, parentStyleNode, children);
    }

    /**
     * Process a box node that represents a paragraph.
     *
     * @param node            to process
     * @param parentStyleNode the style node of the parent
     * @param styleModel      the style model
     * @param referenceModel  the reference model
     * @return the box node as a document node
     */
    private DocumentNode processBoxNode(BoxNode node, DocumentNodeStyle parentStyleNode, StyleModel styleModel, ReferenceModel referenceModel) throws DocumentBuildException {
        // Find child node that may be a thingy node -> in that case we can apply special styles from the style model
        Optional<ThingyNode> optionalThingyNode = getFirstThingyNodeInBox(node);
        String blockName = null;
        if (optionalThingyNode.isPresent()) {
            blockName = optionalThingyNode.get().getName();
        }

        StyleBlock styleBlock = getStyleBlock("PARAGRAPH", blockName, styleModel);

        DocumentNodeStyle styleNode = new DocumentNodeStyle(parentStyleNode, styleBlock.getStyles());

        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processNode(child, styleNode, styleModel, referenceModel));
            }
        }

        return new DocumentNode(node, styleNode, children);
    }

    /**
     * Process another node.
     *
     * @param node            to process
     * @param parentStyleNode the parent style node
     * @param styleModel      the style model
     * @param referenceModel  the reference model
     * @return a document node
     */
    private DocumentNode processNode(Node node, DocumentNodeStyle parentStyleNode, StyleModel styleModel, ReferenceModel referenceModel) throws DocumentBuildException {
        DocumentNodeStyle style = new DocumentNodeStyle(parentStyleNode, null); // Empty node style TODO Allow styling every node?

        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processNode(child, style, styleModel, referenceModel));
            }
        }

        DocumentNode documentNode = new DocumentNode(node, style, children);

        if (node.getType() == NodeType.THINGY) {
            processThingy((ThingyNode) node, documentNode, styleModel, referenceModel);
        }

        return documentNode;
    }

    /**
     * Process a thingy.
     *
     * @param thingyNode     to process
     * @param documentNode   the document node the thingy node is contained in
     * @param styleModel     to modify (optionally)
     * @param referenceModel to modify (optionally)
     */
    private void processThingy(ThingyNode thingyNode, DocumentNode documentNode, StyleModel styleModel, ReferenceModel referenceModel) throws DocumentBuildException {
        String label = thingyNode.getOptions().get("label");
        if (label != null) {
            // Add as potential target
            labelToNodeID.put(label, documentNode.getId());
        }

        String name = thingyNode.getName().toLowerCase();

        switch (name) {
            case "ref" -> {
                // Add potential internal reference
                if (thingyNode.getArguments().isEmpty()) {
                    // Expected a target label
                    throw new MissingReferenceTargetException(String.format(
                            "Expected #REF# Thingy to include a reference target label as first argument at %s",
                            thingyNode.getTextPosition()
                    ));
                }

                String targetLabel = thingyNode.getArguments().iterator().next();
                potentialReferences.add(new PotentialInternalReference(
                        documentNode.getId(),
                        targetLabel,
                        thingyNode.getOptions().get("prefix"),
                        thingyNode.getOptions().get("counter")
                ));
            }
            case "href" -> {
                // Add external reference to the reference model
                if (thingyNode.getArguments().isEmpty()) {
                    // Expected a target URL
                    throw new MissingReferenceTargetException(String.format(
                            "Expected #HREF# Thingy to include a reference target URL as first argument at %s",
                            thingyNode.getTextPosition()
                    ));
                }

                String targetURL = thingyNode.getArguments().iterator().next();

                String displayName = thingyNode.getOptions().get("name");
                if (displayName != null) {
                    referenceModel.addReference(new ExternalReference(targetURL, documentNode.getId(), displayName));
                } else {
                    referenceModel.addReference(new ExternalReference(targetURL, documentNode.getId()));
                }
            }
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
     * A potential internal reference.
     */
    private static class PotentialInternalReference {

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
