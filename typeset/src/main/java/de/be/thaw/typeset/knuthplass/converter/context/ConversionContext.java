package de.be.thaw.typeset.knuthplass.converter.context;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Context used during the document to Knuth-Plass model conversion.
 */
public class ConversionContext {

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

    public ConversionContext(KnuthPlassTypeSettingConfig config) {
        this.config = config;
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

}
