package de.be.thaw.typeset.knuthplass.util;

import de.be.thaw.typeset.knuthplass.item.Item;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Context used to carry information during executing the Knuth-Plass line breaking algorithm.
 */
public class LineBreakingContext {

    /**
     * List of currently active break points (considered to be potential starting/ending points of a line/page).
     */
    private final List<BreakPoint> activeBreakPoints = new LinkedList<>();

    /**
     * The paragraph to find line breaks in.
     */
    private final TextParagraph paragraph;

    /**
     * Cumulative metrics used by the algorithm.
     */
    private final List<CumulativeMetrics> cumulativeMetrics = new ArrayList<>();

    /**
     * Quality of the line breaking.
     */
    private final int lineBreakingQuality;

    public LineBreakingContext(TextParagraph paragraph, int lineBreakingQuality) {
        this.paragraph = paragraph;
        this.lineBreakingQuality = lineBreakingQuality;

        initializeCumulativeMetrics();
    }

    /**
     * Initialize the cumulative widths, stretchabilities and shrinkabilities.
     */
    private void initializeCumulativeMetrics() {
        double totalWidth = 0;
        double totalStretch = 0;
        double totalShrink = 0;

        for (Item item : paragraph.items()) {
            totalWidth += item.getWidth();
            totalStretch += item.getStretchability() * Math.pow(2, lineBreakingQuality);
            totalShrink += item.getShrinkability();

            cumulativeMetrics.add(new CumulativeMetrics(
                    totalWidth,
                    totalStretch,
                    totalShrink
            ));
        }
    }

    /**
     * Get the total width until the given index.
     *
     * @param index to get total width for
     * @return total width
     */
    public double getTotalWidth(int index) {
        return cumulativeMetrics.get(index).getTotalWidth();
    }

    /**
     * Get the total stretchability until the given index.
     *
     * @param index to get total stretch for
     * @return total stretchability
     */
    public double getTotalStretch(int index) {
        return cumulativeMetrics.get(index).getTotalStretch();
    }

    /**
     * Get the total shrinkability until the given index.
     *
     * @param index to get total stretch for
     * @return total shrinkability
     */
    public double getTotalShrink(int index) {
        return cumulativeMetrics.get(index).getTotalShrink();
    }

    public List<BreakPoint> getActiveBreakPoints() {
        return activeBreakPoints;
    }

    public List<Item> getItems() {
        return paragraph.items();
    }

    /**
     * Get the required line width for the passed line number.
     *
     * @return required line width
     */
    public double getLineWidth(int lineNumber) {
        return paragraph.getLineWidth(lineNumber);
    }

    /**
     * Cumulative metrics for every item of the paragraph.
     */
    private static class CumulativeMetrics {

        /**
         * Total width at this point.
         */
        private final double totalWidth;

        /**
         * Total stretchability at this point.
         */
        private final double totalStretch;

        /**
         * Total shrinkability at this point.
         */
        private final double totalShrink;

        public CumulativeMetrics(double totalWidth, double totalStretch, double totalShrink) {
            this.totalWidth = totalWidth;
            this.totalStretch = totalStretch;
            this.totalShrink = totalShrink;
        }

        public double getTotalWidth() {
            return totalWidth;
        }

        public double getTotalStretch() {
            return totalStretch;
        }

        public double getTotalShrink() {
            return totalShrink;
        }

    }

}
