package de.be.thaw.typeset.knuthplass.converter;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.DocumentConverter;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.EnumerationItemNode;
import de.be.thaw.text.model.tree.impl.EnumerationNode;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.ExplicitBreakHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.FootNoteHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.HeadlineHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.HyperRefHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.ImageHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.MathHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.PageHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.RefHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.TableOfContentsHandler;
import de.be.thaw.typeset.knuthplass.item.impl.box.EnumerationItemStartBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converter of the thaw document to the internal Knuth-Plass algorithm format.
 */
public class KnuthPlassConverter implements DocumentConverter<List<List<Paragraph>>> {

    /**
     * Lookup of thingy handlers by the thingy name of the thingy they are dealing with.
     */
    private static final Map<String, ThingyHandler> THINGY_HANDLER_MAP = new HashMap<>();

    static {
        initThingyHandler(new ExplicitBreakHandler());
        initThingyHandler(new ImageHandler());
        initThingyHandler(new HyperRefHandler());
        initThingyHandler(new RefHandler());
        initThingyHandler(new HeadlineHandler());
        initThingyHandler(new TableOfContentsHandler());
        initThingyHandler(new PageHandler());
        initThingyHandler(new FootNoteHandler());
        initThingyHandler(new MathHandler());
    }

    /**
     * Configuration of the line breaking algorithm.
     */
    private final KnuthPlassTypeSettingConfig config;

    /**
     * The root node to convert.
     */
    private final DocumentNode root;

    public KnuthPlassConverter(DocumentNode root, KnuthPlassTypeSettingConfig config) {
        this.config = config;
        this.root = root;
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

    @Override
    public List<List<Paragraph>> convert(Document document) throws DocumentConversionException {
        ConversionContext ctx = new ConversionContext(config, document);

        initializeForNode(ctx, root);

        // Finalize the last paragraph and list of consecutive paragraphs
        ctx.finalizeParagraph();
        ctx.finalizeConsecutiveParagraphList();

        return ctx.getConsecutiveParagraphLists();
    }

    /**
     * Initialize the paragraphs for the given node.
     *
     * @param ctx  the conversion context
     * @param node to initialize for
     */
    private void initializeForNode(ConversionContext ctx, DocumentNode node) throws DocumentConversionException {
        switch (node.getTextNode().getType()) {
            case BOX -> ctx.initializeNewTextParagraph(node);
            case TEXT, FORMATTED -> initializeTextualNode(ctx, node);
            case ENUMERATION_ITEM -> initializeEnumerationItem(ctx, node);
            case THINGY -> initializeThingy(ctx, node);
        }

        // Process child nodes (if any)
        if (node.hasChildren()) {
            for (DocumentNode child : node.getChildren()) {
                initializeForNode(ctx, child);
            }
        }
    }

    /**
     * Initialize using a node that contains textual content.
     *
     * @param ctx          the conversion context
     * @param documentNode to initialize with
     */
    private void initializeTextualNode(ConversionContext ctx, DocumentNode documentNode) throws DocumentConversionException {
        if (!(ctx.getCurrentParagraph() instanceof TextParagraph)) {
            throw new DocumentConversionException("Expected the current paragraph to be a text paragraph");
        }

        TextParagraph paragraph = (TextParagraph) ctx.getCurrentParagraph();

        Node node = documentNode.getTextNode();

        String value;
        if (node.getType() == NodeType.TEXT) {
            value = ((TextNode) node).getValue();
        } else {
            value = ((FormattedNode) node).getValue();
        }

        ctx.appendTextToParagraph(paragraph, value, documentNode);
    }

    /**
     * Initialize an enumeration item.
     *
     * @param ctx          the conversion context
     * @param documentNode to initialize with
     */
    private void initializeEnumerationItem(ConversionContext ctx, DocumentNode documentNode) throws DocumentConversionException {
        EnumerationItemNode node = (EnumerationItemNode) documentNode.getTextNode();

        assert node.getParent() != null;
        int level = ((EnumerationNode) node.getParent()).getLevel();

        double indent = level * config.getIndentWidth();

        String symbol = "\u2022 "; // TODO Get list item symbol from the node style (when the style model has been implemented)

        FontDetailsSupplier.StringMetrics metrics;
        try {
            metrics = config.getFontDetailsSupplier().measureString(documentNode, -1, symbol);
        } catch (Exception e) {
            throw new DocumentConversionException(e);
        }

        TextParagraph paragraph = ctx.initializeNewTextParagraph(documentNode); // Each enumeration item is a individual paragraph!

        final double defaultLineWidth = config.getPageSize().getWidth() - (config.getPageInsets().getLeft() + config.getPageInsets().getRight());
        final double firstLineWidth = defaultLineWidth - indent + metrics.getWidth();
        final double otherLineWidth = defaultLineWidth - indent;
        paragraph.setLineWidthSupplier(lineNumber -> {
            if (lineNumber == 1) {
                return firstLineWidth;
            } else {
                return otherLineWidth;
            }
        });

        paragraph.addItem(new EnumerationItemStartBox(symbol, metrics, documentNode, indent)); // Adding item symbol
    }

    /**
     * Initialize using a thingy node.
     *
     * @param ctx          the conversion context
     * @param documentNode to initialize with
     */
    private void initializeThingy(ConversionContext ctx, DocumentNode documentNode) throws DocumentConversionException {
        ThingyNode node = (ThingyNode) documentNode.getTextNode();

        ThingyHandler handler = THINGY_HANDLER_MAP.get(node.getName().toLowerCase());
        if (handler != null) {
            handler.handle(node, documentNode, ctx);
        } else {
            // Thingies with label act as reference target and must thus be included as box with width 0!
            String label = node.getOptions().get("label");
            if (label != null) {
                Paragraph paragraph = ctx.getCurrentParagraph();
                if (paragraph instanceof TextParagraph) {
                    ((TextParagraph) paragraph).addItem(new TextBox(
                            "",
                            FontDetailsSupplier.StringMetrics.placeholder(),
                            documentNode
                    ));
                }
            }
        }
    }

}
