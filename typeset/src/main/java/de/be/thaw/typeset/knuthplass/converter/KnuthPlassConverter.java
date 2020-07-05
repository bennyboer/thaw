package de.be.thaw.typeset.knuthplass.converter;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.DocumentConverter;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.EnumerationItemNode;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWord;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWordPart;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.EmptyBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Converter of the thaw document to the internal Knuth-Plass algorithm format.
 */
public class KnuthPlassConverter implements DocumentConverter<List<List<Paragraph>>> {

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

        initializeForNode(document.getTextModel().getRoot());

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
    private void initializeForNode(Node node) throws DocumentConversionException {
        switch (node.getType()) {
            case BOX, ENUMERATION -> initializeNewParagraph();
            case TEXT, FORMATTED -> initializeTextualNode(node);
            case ENUMERATION_ITEM -> initializeEnumerationItem((EnumerationItemNode) node);
            case THINGY -> initializeThingy((ThingyNode) node);
        }

        // Process child nodes (if any)
        if (node.hasChildren()) {
            for (Node child : node.children()) {
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
     */
    private void initializeNewParagraph() {
        if (!currentParagraphList.isEmpty()) {
            if (!currentParagraphList.get(currentParagraphList.size() - 1).isEmpty()) {
                finalizeParagraph();
            }
        }

        Paragraph paragraph = new Paragraph();
        paragraph.addItem(new EmptyBox(config.getFirstLineIndent()));

        currentParagraphList.add(paragraph);
    }

    /**
     * Initialize using a node that contains textual content.
     *
     * @param node to initialize with
     */
    private void initializeTextualNode(Node node) throws DocumentConversionException {
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
                    appendWordToParagraph(wordBuffer.toString(), node);
                    wordBuffer.setLength(0); // Reset word buffer

                    // Add inter-word glue (representing a white space)
                    char lastChar = wordBuffer.length() > 0 ? wordBuffer.charAt(wordBuffer.length() - 1) : ' ';

                    try {
                        currentParagraphList.get(currentParagraphList.size() - 1).addItem(new Glue(
                                config.getFontDetailsSupplier().getSpaceWidth(node),
                                config.getGlueConfig().getInterWordStretchability(node, lastChar),
                                config.getGlueConfig().getInterWordShrinkability(node, lastChar)
                        ));
                    } catch (Exception e) {
                        throw new DocumentConversionException(e);
                    }
                }
                case '-' -> {
                    wordBuffer.append(c);

                    // Add as word
                    appendWordToParagraph(wordBuffer.toString(), node);
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
            appendWordToParagraph(wordBuffer.toString(), node);
        }
    }

    /**
     * Append a word to the current paragraph.
     *
     * @param word to append
     * @param node the word belongs to
     */
    private void appendWordToParagraph(String word, Node node) throws DocumentConversionException {
        if (word.isEmpty()) {
            return;
        }

        Paragraph paragraph = currentParagraphList.get(currentParagraphList.size() - 1);

        // Hyphenate word first
        HyphenatedWord hyphenatedWord = config.getHyphenator().hyphenate(word);
        List<HyphenatedWordPart> parts = hyphenatedWord.getParts();

        try {
            int len = parts.size();
            double hyphenWidth = len > 1 ? config.getFontDetailsSupplier().getCodeWidth(node, '-') : 0;

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
                    paragraph.addItem(new Penalty(part.getPenalty(), hyphenWidth, true));
                }
            }
        } catch (Exception e) {
            throw new DocumentConversionException(e);
        }
    }

    /**
     * Initialize an enumeration item.
     *
     * @param node to initialize with
     */
    private void initializeEnumerationItem(EnumerationItemNode node) {
        // TODO Special handling for an enumeration item where an explicit line break is added at the end and an enumeration item at the beginning
    }

    /**
     * Initialize using a thingy node.
     *
     * @param node to initialize with
     */
    private void initializeThingy(ThingyNode node) {
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

        currentParagraphList = new ArrayList<>();
        initializeNewParagraph();
    }

}
