package de.be.thaw.typeset.knuthplass.converter.context;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWord;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWordPart;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.EmptyBox;
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
 * Context used during the document to Knuth-Plass model conversion.
 */
public class ConversionContext {

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
    private final List<List<Paragraph>> consecutiveParagraphLists = new ArrayList<>();

    /**
     * Consecutive paragraphs (without page breaks).
     */
    private List<Paragraph> currentParagraphList = new ArrayList<>();

    /**
     * The current paragraph.
     */
    private Paragraph currentParagraph;

    /**
     * The document to convert.
     */
    private final Document document;

    /**
     * Counter for internal references.
     * Mapped by different counter names.
     */
    private final Map<String, Integer> internalReferenceCounter = new HashMap<>();

    public ConversionContext(KnuthPlassTypeSettingConfig config, Document document) {
        this.config = config;
        this.document = document;
    }

    /**
     * Get the document to convert.
     *
     * @return document
     */
    public Document getDocument() {
        return document;
    }

    public KnuthPlassTypeSettingConfig getConfig() {
        return config;
    }

    public List<List<Paragraph>> getConsecutiveParagraphLists() {
        return consecutiveParagraphLists;
    }

    public List<Paragraph> getCurrentParagraphList() {
        return currentParagraphList;
    }

    /**
     * Get the current paragraph of null if there is none.
     *
     * @return the current paragraph
     */
    public Paragraph getCurrentParagraph() {
        return currentParagraph;
    }

    /**
     * Finalize the current paragraph.
     */
    public void finalizeParagraph() {
        Paragraph current = getCurrentParagraph();
        if (current == null) {
            return;
        }

        if (current instanceof TextParagraph) {
            TextParagraph textParagraph = (TextParagraph) current;

            if (!textParagraph.isEmpty()) {
                // Add glue as stretchable space to fill the last line
                textParagraph.addItem(new Glue(0, Double.POSITIVE_INFINITY, 0));

                // Add explicit line break
                textParagraph.addItem(new Penalty(Double.NEGATIVE_INFINITY, 0, true));

                currentParagraphList.add(textParagraph);
            }
        } else {
            currentParagraphList.add(current);
        }

        currentParagraph = null; // Reset current paragraph
    }

    /**
     * Finalize the current consecutive paragraph list.
     */
    public void finalizeConsecutiveParagraphList() {
        if (!currentParagraphList.isEmpty()) {
            consecutiveParagraphLists.add(currentParagraphList);

            currentParagraphList = new ArrayList<>();
        }
    }

    /**
     * Set the current paragraph to process.
     *
     * @param paragraph to proces
     */
    public void setCurrentParagraph(Paragraph paragraph) {
        this.currentParagraph = paragraph;
    }

    /**
     * Get the available line width.
     *
     * @return available line width
     */
    public double getLineWidth() {
        return config.getPageSize().getWidth() - (config.getPageInsets().getLeft() + config.getPageInsets().getRight());
    }

    /**
     * Initialize a new text paragraph.
     *
     * @param node representing the paragraph
     * @return the new text paragraph
     */
    public TextParagraph initializeNewTextParagraph(DocumentNode node) {
        finalizeParagraph();

        TextParagraph paragraph = new TextParagraph(getLineWidth(), node);

        setCurrentParagraph(paragraph);

        return paragraph;
    }

    /**
     * Append string that may contain one or more words to the passed text paragraph.
     *
     * @param paragraph to add string to
     * @param str       the string to add
     * @param node      of the string
     * @throws DocumentConversionException in case the text could not be appended properly
     */
    public void appendTextToParagraph(TextParagraph paragraph, String str, DocumentNode node) throws DocumentConversionException {
        // Find all words
        StringBuilder wordBuffer = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);

            switch (c) {
                case ' ' -> {
                    appendWordToParagraph(paragraph, wordBuffer.toString(), node);
                    wordBuffer.setLength(0); // Reset word buffer

                    // Add inter-word glue (representing a white space)
                    char lastChar = ' ';

                    try {
                        paragraph.addItem(new Glue(
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
                    appendWordToParagraph(paragraph, wordBuffer.toString(), node);
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
            appendWordToParagraph(paragraph, wordBuffer.toString(), node);
        }
    }

    /**
     * Append a word to the current paragraph.
     *
     * @param paragraph to add word to
     * @param word      to append
     * @param node      the word belongs to
     */
    public void appendWordToParagraph(TextParagraph paragraph, String word, DocumentNode node) throws DocumentConversionException {
        if (word.isEmpty()) {
            return;
        }

        if (paragraph.isEmpty()) {
            appendEmptyBoxToParagraph(node, paragraph);
        }

        // Split by punctuation characters
        List<WordPartSplitByPunctuationCharacter> splitWord = splitByPunctuationCharacter(word);

        try {
            int lastChar = -1;
            for (WordPartSplitByPunctuationCharacter splitWordPart : splitWord) {
                if (splitWordPart.isPunctuationCharacter()) {
                    FontDetailsSupplier.StringMetrics metrics = config.getFontDetailsSupplier().measureString(node, lastChar, splitWordPart.getPart());

                    paragraph.addItem(new TextBox(
                            splitWordPart.getPart(),
                            metrics.getWidth(),
                            metrics.getFontSize(),
                            metrics.getKerningAdjustments(),
                            node
                    ));
                } else { // Is an actual word without punctuation characters
                    // Hyphenate word first
                    HyphenatedWord hyphenatedWord = config.getHyphenator().hyphenate(splitWordPart.getPart());
                    List<HyphenatedWordPart> parts = hyphenatedWord.getParts();

                    int len = parts.size();
                    double hyphenWidth = len > 1 ? config.getFontDetailsSupplier().measureString(node, lastChar, "-").getWidth() : 0;

                    for (int i = 0; i < len; i++) {
                        HyphenatedWordPart part = parts.get(i);

                        FontDetailsSupplier.StringMetrics metrics = config.getFontDetailsSupplier().measureString(node, lastChar, part.getPart());
                        paragraph.addItem(new TextBox(
                                part.getPart(),
                                metrics.getWidth(),
                                metrics.getFontSize(),
                                metrics.getKerningAdjustments(),
                                node
                        ));

                        lastChar = getLastCodePoint(part.getPart());

                        boolean isLast = i == len - 1;
                        if (!isLast) {
                            // Add hyphen penalty to represent an optional hyphen
                            paragraph.addItem(new Penalty(part.getPenalty(), hyphenWidth, true, node));
                        }
                    }
                }

                lastChar = getLastCodePoint(splitWordPart.getPart());
            }
        } catch (Exception e) {
            throw new DocumentConversionException(e);
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
     * Get the last code point in the given string.
     *
     * @param str to get last code point in
     * @return last code point
     */
    private int getLastCodePoint(String str) {
        final int len = str.length();
        int codePoint = -1;

        for (int i = 0; i < len; ) {
            codePoint = str.codePointAt(i);

            i += Character.charCount(codePoint);
        }

        return codePoint;
    }

    /**
     * Get and increment the internal reference counter.
     *
     * @param counterName name of the counter to get and increment
     * @return the reference count
     */
    public int getAndIncrementInternalRefCounter(String counterName) {
        int counter = internalReferenceCounter.computeIfAbsent(counterName, k -> 0) + 1;
        internalReferenceCounter.put(counterName, counter);

        return counter;
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
