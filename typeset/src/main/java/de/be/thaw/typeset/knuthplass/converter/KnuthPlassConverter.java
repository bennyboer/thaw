package de.be.thaw.typeset.knuthplass.converter;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.DocumentConverter;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.FirstLineIndentStyle;
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
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.EmptyBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.EnumerationItemStartBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;

import java.util.ArrayList;
import java.util.List;
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
     * Configuration of the line breaking algorithm.
     */
    private final KnuthPlassTypeSettingConfig config;

    /**
     * The collected consecutive paragraph lists (separated by page breaks).
     */
    private final List<List<Paragraph>> paragraphs = new ArrayList<>();

    /**
     * Consecutive paragraphs (without page breaks).
     */
    private List<Paragraph> currentParagraphList;

    public KnuthPlassConverter(KnuthPlassTypeSettingConfig config) {
        this.config = config;
    }

    @Override
    public List<List<Paragraph>> convert(Document document) throws DocumentConversionException {
        paragraphs.clear();
        currentParagraphList = new ArrayList<>();

        initializeForNode(document.getRoot());

        finalizeParagraph(); // Finalize the last paragraph

        if (!currentParagraphList.isEmpty()) {
            paragraphs.add(currentParagraphList);
        }

        return paragraphs;
    }

    /**
     * Initialize the paragraphs for the given node.
     *
     * @param node to initialize for
     */
    private void initializeForNode(DocumentNode node) throws DocumentConversionException {
        switch (node.getTextNode().getType()) {
            case BOX -> initializeNewParagraph(node);
            case TEXT, FORMATTED -> initializeTextualNode(node);
            case ENUMERATION_ITEM -> initializeEnumerationItem(node);
            case THINGY -> initializeThingy(node);
        }

        // Process child nodes (if any)
        if (node.hasChildren()) {
            for (DocumentNode child : node.getChildren()) {
                initializeForNode(child);
            }
        }
    }

    /**
     * Finalize the current paragraph.
     */
    private void finalizeParagraph() {
        if (currentParagraphList.isEmpty()) {
            return;
        }

        Paragraph current = currentParagraphList.get(currentParagraphList.size() - 1);

        // Add glue as stretchable space to fill the last line
        current.addItem(new Glue(0, Double.POSITIVE_INFINITY, 0));

        // Add explicit line break
        current.addItem(new Penalty(Double.NEGATIVE_INFINITY, 0, true));
    }

    /**
     * Initialize a new paragraph.
     *
     * @param node representing the paragraph
     */
    private void initializeNewParagraph(DocumentNode node) {
        if (!currentParagraphList.isEmpty()) {
            if (!currentParagraphList.get(currentParagraphList.size() - 1).isEmpty()) {
                finalizeParagraph();
            }
        }

        double lineWidth = config.getPageSize().getWidth() - (config.getPageInsets().getLeft() + config.getPageInsets().getRight());
        Paragraph paragraph = new Paragraph(lineWidth, node);

        currentParagraphList.add(paragraph);
    }

    /**
     * Initialize using a node that contains textual content.
     *
     * @param documentNode to initialize with
     */
    private void initializeTextualNode(DocumentNode documentNode) throws DocumentConversionException {
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
                    appendWordToParagraph(wordBuffer.toString(), documentNode);
                    wordBuffer.setLength(0); // Reset word buffer

                    // Add inter-word glue (representing a white space)
                    char lastChar = ' ';

                    try {
                        currentParagraphList.get(currentParagraphList.size() - 1).addItem(new Glue(
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
                    appendWordToParagraph(wordBuffer.toString(), documentNode);
                    wordBuffer.setLength(0); // Reset word buffer

                    // Add explicit hyphen to the paragraph
                    currentParagraphList.get(currentParagraphList.size() - 1).addItem(new Penalty(
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
            appendWordToParagraph(wordBuffer.toString(), documentNode);
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
     * @param node of the paragraph
     */
    private void appendEmptyBoxToParagraph(DocumentNode node) {
        Paragraph paragraph = currentParagraphList.get(currentParagraphList.size() - 1);
        if (paragraph.isEmpty()) {
            double firstLineIndent = node.getStyle().getStyleAttribute(
                    StyleType.FIRST_LINE_INDENT,
                    style -> Optional.of(((FirstLineIndentStyle) style).getIndent())
            ).orElse(0.0);

            paragraph.addItem(new EmptyBox(firstLineIndent));
        }
    }

    /**
     * Append a word to the current paragraph.
     *
     * @param word to append
     * @param node the word belongs to
     */
    private void appendWordToParagraph(String word, DocumentNode node) throws DocumentConversionException {
        if (word.isEmpty()) {
            return;
        }

        appendEmptyBoxToParagraph(node);

        // Split by punctuation characters
        List<WordPartSplitByPunctuationCharacter> splitWord = splitByPunctuationCharacter(word);

        Paragraph paragraph = currentParagraphList.get(currentParagraphList.size() - 1);

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
     * @param documentNode to initialize with
     */
    private void initializeEnumerationItem(DocumentNode documentNode) throws DocumentConversionException {
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

        initializeNewParagraph(documentNode); // Each enumeration item is a individual paragraph!

        Paragraph paragraph = currentParagraphList.get(currentParagraphList.size() - 1);

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
     * @param documentNode to initialize with
     */
    private void initializeThingy(DocumentNode documentNode) {
        ThingyNode node = (ThingyNode) documentNode.getTextNode();

        // Checking for explicit line or page breaks
        if (node.getName().equalsIgnoreCase("BREAK")) {
            boolean isPageBreak = Optional.ofNullable(node.getOptions().get("type"))
                    .orElse("LINE")
                    .equalsIgnoreCase("PAGE");

            if (isPageBreak) {
                onPageBreak();
            } else {
                onLineBreak();
            }
        }
    }

    /**
     * Called on an explicit line break.
     */
    private void onLineBreak() {
        Paragraph current = currentParagraphList.get(currentParagraphList.size() - 1);

        // Add glue as stretchable space to fill the last line
        current.addItem(new Glue(0, config.getPageSize().getWidth(), 0));

        // Add explicit line break
        current.addItem(new Penalty(Double.NEGATIVE_INFINITY, 0, true));
    }

    /**
     * Called on an explicit page break.
     */
    private void onPageBreak() {
        // Create a new consecutive paragraph list after finalizing the current paragraph
        if (!currentParagraphList.isEmpty()) {
            if (!currentParagraphList.get(currentParagraphList.size() - 1).isEmpty()) {
                finalizeParagraph();
            }
        }

        if (!currentParagraphList.isEmpty()) {
            paragraphs.add(currentParagraphList);
        }

        Paragraph lastParagraph = currentParagraphList.get(currentParagraphList.size() - 1);
        currentParagraphList = new ArrayList<>();
        initializeNewParagraph(lastParagraph.getNode());
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
