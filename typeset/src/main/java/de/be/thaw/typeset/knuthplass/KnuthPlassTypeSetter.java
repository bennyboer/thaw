package de.be.thaw.typeset.knuthplass;

import de.be.thaw.core.document.Document;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.knuthplass.config.LineBreakingConfig;
import de.be.thaw.typeset.knuthplass.converter.KnuthPlassConverter;
import de.be.thaw.typeset.knuthplass.item.Item;
import de.be.thaw.typeset.knuthplass.item.ItemType;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.util.ActiveBreakPoint;
import de.be.thaw.typeset.knuthplass.util.BreakPoint;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the Knuth-Plass line breaking algorithm.
 */
public class KnuthPlassTypeSetter implements TypeSetter {

    /**
     * Configuration of the
     */
    private final LineBreakingConfig config;

    /**
     * Paragraphs to type set.
     */
    private List<Paragraph> paragraphs;

    /**
     * The currently typeset pages.
     */
    private List<Page> pages;

    /**
     * Elements of the current page.
     */
    private List<Element> currentPageElements;

    public KnuthPlassTypeSetter(LineBreakingConfig config) {
        this.config = config;
    }

    @Override
    public List<Page> typeset(Document document) {
        paragraphs = new KnuthPlassConverter(config).convert(document);

        pages = new ArrayList<>();
        currentPageElements = new ArrayList<>();

        for (Paragraph paragraph : paragraphs) {
            List<BreakPoint> breakPoints = findBreakPoints(paragraph);

            List<ActiveBreakPoint> activeBreakPoints = new LinkedList<>();
            activeBreakPoints.add(new ActiveBreakPoint(breakPoints.get(0)));

            // TODO
        }

        return null;
    }

    /**
     * Find a list of break points in the passed paragraph.
     *
     * @param paragraph to find break points in
     * @return a list of break points
     */
    private List<BreakPoint> findBreakPoints(Paragraph paragraph) {
        List<BreakPoint> breakPoints = new ArrayList<>();

        // Add break point at the beginning of the paragraph.
        breakPoints.add(new BreakPoint(0, 0));

        int len = paragraph.items().size();
        for (int i = 0; i < len; i++) {
            Item item = paragraph.items().get(i);

            if (item.getType() == ItemType.PENALTY) {
                Penalty penalty = (Penalty) item;

                if (!penalty.isImpossibleLineBreak()) {
                    breakPoints.add(new BreakPoint(i, penalty.getPenalty()));
                }
            }
        }

        return breakPoints;
    }

}
