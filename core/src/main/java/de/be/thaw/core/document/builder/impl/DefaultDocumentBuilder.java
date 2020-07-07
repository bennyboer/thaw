package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.DocumentBuilder;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.BackgroundStyle;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FirstLineIndentStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.LineHeightStyle;
import de.be.thaw.style.model.style.impl.SizeStyle;
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

    @Override
    public Document build(DocumentBuildSource source) {
        return new Document(source.getInfo(), toRootNode(source.getTextModel(), source.getStyleModel()));
    }

    /**
     * Convert the passed models to a document node.
     *
     * @param textModel  to convert
     * @param styleModel to convert
     * @return the root document node
     */
    private DocumentNode toRootNode(TextModel textModel, StyleModel styleModel) {
        return processRootNode(
                textModel.getRoot(),
                new DocumentNodeStyle(null, styleModel.getBlock("DOCUMENT").orElseGet(this::getDefaultDocumentStyleBlock).getStyles()),
                styleModel
        );
    }

    /**
     * Convert to a root node.
     *
     * @param node            to convert
     * @param parentStyleNode the parent style node
     * @param styleModel      to convert
     * @return the root document node
     */
    private DocumentNode processRootNode(RootNode node, DocumentNodeStyle parentStyleNode, StyleModel styleModel) {
        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processBoxNode((BoxNode) child, parentStyleNode, styleModel));
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
     * @return the box node as a document node
     */
    private DocumentNode processBoxNode(BoxNode node, DocumentNodeStyle parentStyleNode, StyleModel styleModel) {
        // Find child node that may be a thingy node -> in that case we can apply special styles from the style model
        Optional<ThingyNode> optionalThingyNode = getFirstThingyNodeInBox(node);
        String blockName = "PARAGRAPH";
        if (optionalThingyNode.isPresent()) {
            blockName = optionalThingyNode.get().getName();
        }

        StyleBlock styleBlock = styleModel.getBlock(blockName).orElse(getDefaultParagraphStyleBlock(styleModel));

        DocumentNodeStyle styleNode = new DocumentNodeStyle(parentStyleNode, styleBlock.getStyles());

        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processNode(child, styleNode, styleModel));
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
     * @return a document node
     */
    private DocumentNode processNode(Node node, DocumentNodeStyle parentStyleNode, StyleModel styleModel) {
        DocumentNodeStyle style = new DocumentNodeStyle(parentStyleNode, null); // Empty node style TODO Allow styling every node?

        List<DocumentNode> children = new ArrayList<>();
        if (node.hasChildren()) {
            for (Node child : node.children()) {
                children.add(processNode(child, style, styleModel));
            }
        }

        return new DocumentNode(node, style, children);
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
     * Get the default document style block.
     *
     * @return document style block
     */
    private StyleBlock getDefaultDocumentStyleBlock() {
        Map<StyleType, Style> styles = new HashMap<>();

        styles.put(StyleType.SIZE, new SizeStyle(210, 297));
        styles.put(StyleType.INSETS, new InsetsStyle(20, 25, 20, 25));
        styles.put(StyleType.BACKGROUND, new BackgroundStyle(new ColorStyle(1.0, 1.0, 1.0, 1.0)));
        styles.put(StyleType.FONT, new FontStyle("Cambria", FontVariant.PLAIN, 12.0, new ColorStyle(0, 0, 0, 1.0)));
        styles.put(StyleType.FIRST_LINE_INDENT, new FirstLineIndentStyle(8));
        styles.put(StyleType.LINE_HEIGHT, new LineHeightStyle(1.5));

        return new StyleBlock("DOCUMENT", styles);
    }

    /**
     * Get the default paragraph style block.
     *
     * @return paragraph style block
     */
    private StyleBlock getDefaultParagraphStyleBlock(StyleModel styleModel) {
        Map<StyleType, Style> styles = new HashMap<>();

        styles.put(StyleType.INSETS, new InsetsStyle(0, 0, 2, 0));

        styleModel.getBlock("PARAGRAPH").ifPresent(styleBlock -> {
            for (Map.Entry<StyleType, Style> styleEntry : styleBlock.getStyles().entrySet()) {
                styles.put(styleEntry.getKey(), styleEntry.getValue());
            }
        });

        return new StyleBlock("PARAGRAPH", styles);
    }

}
