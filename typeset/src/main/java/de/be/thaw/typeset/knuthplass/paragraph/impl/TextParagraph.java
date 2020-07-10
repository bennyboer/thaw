package de.be.thaw.typeset.knuthplass.paragraph.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.item.Item;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;

import java.util.ArrayList;
import java.util.List;

/**
 * A paragraph representation to process using the Knuth-Plass line breaking algorithm.
 */
public class TextParagraph extends AbstractParagraph {

    /**
     * The items the paragraph consists of (boxes, glues and penalties).
     */
    private final List<Item> items = new ArrayList<>();

    public TextParagraph(double lineWidth, DocumentNode node) {
        super(lineWidth, node);
    }

    /**
     * Add an item to the paragraph.
     *
     * @param item to add
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Get the items of the paragraph.
     *
     * @return items
     */
    public List<Item> items() {
        return items;
    }

    /**
     * Check whether the paragraph does not have items yet.
     *
     * @return whether empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.TEXT;
    }

}
