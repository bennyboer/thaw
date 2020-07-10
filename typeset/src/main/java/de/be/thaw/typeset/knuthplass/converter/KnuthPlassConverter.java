package de.be.thaw.typeset.knuthplass.converter;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.DocumentConverter;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.EnumerationItemNode;
import de.be.thaw.text.model.tree.impl.EnumerationNode;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWord;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWordPart;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.ExplicitBreakHandler;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl.ImageHandler;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.EmptyBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.EnumerationItemStartBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Converter of the thaw document to the internal Knuth-Plass algorithm format.
 */
public class KnuthPlassConverter implements DocumentConverter<List<List<Paragraph>>> {

    /**
     * Pattern matching punctuation characters.
     */
    private static final Pattern PUNCTUATION_CHARACTER_PATTERN = Pattern.compile("[.,;!?'\"\\u201E\\u201C\\u201D\\u201F\\u201A\\u2019\\u2018\\u00AB\\u00BB]");

    /**
     * Lookup of thingy handlers by the thingy name of the thingy they are dealing with.
     */
    private static final Map<String, ThingyHandler> THINGY_HANDLER_MAP = new HashMap<>();

    static {
        initThingyHandler(new ExplicitBreakHandler());
        initThingyHandler(new ImageHandler());
    }

    /**
     * Configuration of the line breaking algorithm.
     */
    private final KnuthPlassTypeSettingConfig config;

    public KnuthPlassConverter(KnuthPlassTypeSettingConfig config) {
        this.config = config;
    }

    /**
     * Initialize the passed thingy handler.
     *
     * @param handler to initialize
     */
    private static void initThingyHandler(ThingyHandler handler) {
        THINGY_HANDLER_MAP.put(handler.getThingyName().toLowerCase(), handler);
    }

    @Override
    public List<List<Paragraph>> convert(Document document) throws DocumentConversionException {
        ConversionContext ctx = new ConversionContext(config);

        initializeForNode(ctx, document.getRoot());

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

        // Find all words
        StringBuilder wordBuffer = new StringBuilder();
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);

            switch (c) {
                case ' ' -> {
                    appendWordToParagraph(paragraph, wordBuffer.toString(), documentNode);
                    wordBuffer.setLength(0); // Reset word buffer

                    // Add inter-word glue (representing a white space)
                    char lastChar = ' ';

                    try {
                        paragraph.addItem(new Glue(
                                config.getFontDetailsSupplier().getSpaceWidth(documentNode),
                                config.getGlueConfig().getInterWordStretchability(documentNode, lastChar),
                                config.getGlueConfig().getInterWordShrinkability(documentNode, lastChar)
                        ));
                    } catch (Exception e) {
                        throw new DocumentConversionException(e);
                    }
                }
                case '-' -> {
                    wordBuffer.append(c);

                    // Add as word
                    appendWordToParagraph(paragraph, wordBuffer.toString(), documentNode);
                    wordBuffer.setLength(0); // Reset word buffer

                    // Add explicit hyphen to the paragraph
                    paragraph.addItem(new Penalty(
                            config.getHyphenator().getExplicitHyphenPenalty(),
                            0,
                            true
                    ));
                }
                default -> wordBuffer.append(c);
            }
        }

        // Add final item
        if (wordBuffer.length() > 0) {
            appendWordToParagraph(paragraph, wordBuffer.toString(), documentNode);
        }
    }

    /**
     * Split the passed word by punctuation characters.
     *
     * @param str to split
     * @return the split word
     */
    private List<WordPartSplitByPunctuationCharacter> splitByPunctuationCharacter(String str) {
        String replaced = PUNCTUATION_CHARACTER_PATTERN.matcher(str).replaceAll(" ");

        List<WordPartSplitByPunctuationCharacter> splitWord = new ArrayList<>();

        int len = replaced.length();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c = replaced.charAt(i);

            if (c == ' ') {
                if (buffer.length() > 0) {
                    splitWord.add(new WordPartSplitByPunctuationCharacter(false, buffer.toString()));
                    buffer.setLength(0); // Reset buffer
                }

                // Has been punctuation mark
                splitWord.add(new WordPartSplitByPunctuationCharacter(true, String.valueOf(str.charAt(i))));
            } else {
                buffer.append(c);
            }
        }

        if (buffer.length() > 0) {
            splitWord.add(new WordPartSplitByPunctuationCharacter(false, buffer.toString()));
        }

        return splitWord;
    }

    /**
     * Append an empty box to represent a first line indent to the current paragraph.
     *
     * @param node      of the paragraph
     * @param paragraph to append empty box to
     */
    private void appendEmptyBoxToParagraph(DocumentNode node, TextParagraph paragraph) {
        double firstLineIndent = node.getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.of(((TextStyle) style).getFirstLineIndent())
        ).orElse(0.0);

        paragraph.addItem(new EmptyBox(firstLineIndent));
    }

    /**
     * Append a word to the current paragraph.
     *
     * @param paragraph to add word to
     * @param word      to append
     * @param node      the word belongs to
     */
    private void appendWordToParagraph(TextParagraph paragraph, String word, DocumentNode node) throws DocumentConversionException {
        if (word.isEmpty()) {
            return;
        }

        if (paragraph.isEmpty()) {
            appendEmptyBoxToParagraph(node, paragraph);
        }

        // Split by punctuation characters
        List<WordPartSplitByPunctuationCharacter> splitWord = splitByPunctuationCharacter(word);

        try {
            for (WordPartSplitByPunctuationCharacter splitWordPart : splitWord) {
                if (splitWordPart.isPunctuationCharacter()) {
                    paragraph.addItem(new TextBox(
                            splitWordPart.getPart(),
                            config.getFontDetailsSupplier().getStringWidth(node, splitWordPart.getPart()),
                            node
                    ));
                } else { // Is an actual word without punctuation characters
                    // Hyphenate word first
                    HyphenatedWord hyphenatedWord = config.getHyphenator().hyphenate(splitWordPart.getPart());
                    List<HyphenatedWordPart> parts = hyphenatedWord.getParts();

                    int len = parts.size();
                    double hyphenWidth = len > 1 ? config.getFontDetailsSupplier().getStringWidth(node, "-") : 0;

                    for (int i = 0; i < len; i++) {
                        HyphenatedWordPart part = parts.get(i);

                        paragraph.addItem(new TextBox(
                                part.getPart(),
                                config.getFontDetailsSupplier().getStringWidth(node, part.getPart()),
                                node
                        ));

                        boolean isLast = i == len - 1;
                        if (!isLast) {
                            // Add hyphen penalty to represent an optional hyphen
                            paragraph.addItem(new Penalty(part.getPenalty(), hyphenWidth, true, node));
                        }
                    }

                }
            }
        } catch (Exception e) {
            throw new DocumentConversionException(e);
        }
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
        double symbolWidth;
        try {
            symbolWidth = config.getFontDetailsSupplier().getStringWidth(documentNode, symbol);
        } catch (Exception e) {
            throw new DocumentConversionException(e);
        }

        TextParagraph paragraph = ctx.initializeNewTextParagraph(documentNode); // Each enumeration item is a individual paragraph!

        final double defaultLineWidth = config.getPageSize().getWidth() - (config.getPageInsets().getLeft() + config.getPageInsets().getRight());
        final double firstLineWidth = defaultLineWidth - indent + symbolWidth;
        final double otherLineWidth = defaultLineWidth - indent;
        paragraph.setLineWidthSupplier(lineNumber -> {
            if (lineNumber == 1) {
                return firstLineWidth;
            } else {
                return otherLineWidth;
            }
        });

        paragraph.addItem(new EnumerationItemStartBox(symbol, symbolWidth, documentNode, indent)); // Adding item symbol
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
        }
    }

    /**
     * A word part that is split by punctuation character.
     */
    private static class WordPartSplitByPunctuationCharacter {

        /**
         * Whether the contained word part is a punctuation character.
         */
        private final boolean isPunctuationCharacter;

        /**
         * The contained word part that is either a punctuation character or a legal word part.
         */
        private final String part;

        public WordPartSplitByPunctuationCharacter(boolean isPunctuationCharacter, String part) {
            this.isPunctuationCharacter = isPunctuationCharacter;
            this.part = part;
        }

        public boolean isPunctuationCharacter() {
            return isPunctuationCharacter;
        }

        public String getPart() {
            return part;
        }

    }

}
