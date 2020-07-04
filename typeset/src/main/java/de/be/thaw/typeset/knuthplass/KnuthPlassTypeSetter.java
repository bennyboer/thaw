package de.be.thaw.typeset.knuthplass;

import de.be.thaw.core.document.Document;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.config.LineBreakingConfig;
import de.be.thaw.typeset.knuthplass.converter.KnuthPlassConverter;
import de.be.thaw.typeset.knuthplass.exception.CouldNotFindFeasibleSolutionException;
import de.be.thaw.typeset.knuthplass.item.Item;
import de.be.thaw.typeset.knuthplass.item.ItemType;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.util.BreakPoint;
import de.be.thaw.typeset.knuthplass.util.LineBreakingContext;
import de.be.thaw.typeset.knuthplass.util.LineFit;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<Page> typeset(Document document) throws TypeSettingException {
        paragraphs = new KnuthPlassConverter(config).convert(document);

        pages = new ArrayList<>();
        currentPageElements = new ArrayList<>();

        // Current x and y offsets
        double x = 0;
        double y = 0;

        for (Paragraph paragraph : paragraphs) {
            LineBreakingResult result;
            try {
                result = findBreakPoints(paragraph, config.getPageSize().getWidth());
            } catch (CouldNotFindFeasibleSolutionException e) {
                throw new TypeSettingException("Typesetting failed because the line breaking algorithm could not find a feasible solution", e);
            }

            // Split paragraph into lines
            List<List<Item>> lines = new ArrayList<>();
            List<Item> currentLine = new ArrayList<>();
            lines.add(currentLine);

            int nextBreakPointIndex = 0;
            int len = paragraph.items().size();
            for (int i = 0; i < len; i++) {
                Item item = paragraph.items().get(i);

                if (result.getBreakPoints().size() > nextBreakPointIndex
                        && i == result.getBreakPoints().get(nextBreakPointIndex).getIndex()) {
                    nextBreakPointIndex++;

                    // Push new line
                    currentLine = new ArrayList<>();
                    lines.add(currentLine);
                } else {
                    currentLine.add(item);
                }
            }

            // Remove the last empty line
            if (lines.get(lines.size() - 1).isEmpty()) {
                lines.remove(lines.size() - 1);
            }

            for (int i = 0; i < lines.size(); i++) {
                if (y > config.getPageSize().getHeight()) {
                    // Create next page
                    pages.add(new Page(pages.size() + 1, currentPageElements));
                    currentPageElements = new ArrayList<>();
                    y = 0;
                }

                double lineWidth = result.getContext().getLineWidth(i + 1);
                List<Item> line = lines.get(i);
                LineMetrics lineMetrics = calculateLineMetrics(line);

                if (lineMetrics.getMinWidth() == 0) {
                    // No need to lay it out.
                    // Might happen when breaking right before the paragraphs end glue with
                    // zero width and infinite stretchability.
                    continue;
                }

                // TODO Configuration whether justifying and what alignment needs to be applied here!

                boolean isLastLine = i == lines.size() - 1;
                double spaceWidth = getJustifiedLineSpaceWidth(lineMetrics, lineWidth);

                for (Item item : line) {
                    if (item instanceof TextBox) {
                        currentPageElements.add(new TextElement(
                                ((TextBox) item).getText(),
                                ((TextBox) item).getNode(),
                                new Size(item.getWidth(), config.getLineHeight()),
                                new Position(x, y)
                        ));

                        x += item.getWidth();
                    } else if (item instanceof Penalty) {
                        if (item.isFlagged() && item.getWidth() > 0) {
                            currentPageElements.add(new TextElement(
                                    "-",
                                    null,
                                    new Size(item.getWidth(), config.getLineHeight()),
                                    new Position(x, y)
                            ));

                            x += item.getWidth() + (isLastLine ? 0 : spaceWidth);
                        }
                    } else if (item instanceof Glue) {
                        if (item.getWidth() > 0) {
                            x += isLastLine ? item.getWidth() : spaceWidth;
                        }
                    } else {
                        x += item.getWidth();
                    }
                }

                y += config.getLineHeight();
                x = 0;
            }
        }

        // Push last page
        pages.add(new Page(pages.size() + 1, currentPageElements));

        return pages;
    }

    /**
     * Calculate some metrics that describe the passed line.
     *
     * @param line to calculate metrics for
     * @return line metrics
     */
    private LineMetrics calculateLineMetrics(List<Item> line) {
        double totalWidth = 0;
        int glueCount = 0; // Amount of glues (white spaces)

        for (Item item : line) {
            if (item.getType() != ItemType.GLUE) {
                totalWidth += item.getWidth();
            } else {
                glueCount++;
            }
        }

        return new LineMetrics(totalWidth, glueCount);
    }

    /**
     * Get the width of a space when justifying.
     *
     * @param lineMetrics metrics of the line to get the space width for
     * @param lineWidth   required width of the line
     * @return space width
     */
    private double getJustifiedLineSpaceWidth(LineMetrics lineMetrics, double lineWidth) {
        return (lineWidth - lineMetrics.minWidth) / lineMetrics.getWhiteSpaces();
    }

    /**
     * Find break points for the given paragraph.
     *
     * @param paragraph to find break points for
     * @param lineWidth the line width to try to fit the paragraphs text to
     * @return the found break points
     * @throws CouldNotFindFeasibleSolutionException in case the algorithm could not find a feasible solution
     */
    private LineBreakingResult findBreakPoints(Paragraph paragraph, double lineWidth) throws CouldNotFindFeasibleSolutionException {
        LineBreakingContext ctx = new LineBreakingContext(paragraph.items(), lineWidth);

        // Adding initial active break point representing the beginning of the paragraph
        ctx.getActiveBreakPoints().add(new BreakPoint(0));

        List<BreakPoint> deactivateLater = new ArrayList<>();
        List<BreakPoint> activateLater = new ArrayList<>();

        int len = ctx.getItems().size();
        for (int i = 0; i < len; i++) {
            if (!isLegalBreakPointAt(ctx, i)) {
                continue;
            }

            BreakPoint current = new BreakPoint(i); // b in the original paper

            // Iterate over all currently active break points to find the best fitting ones
            for (BreakPoint activeBreakPoint : ctx.getActiveBreakPoints()) { // activeBreakPoint is a in the original paper
                double adjustmentRatio = computeAdjustmentRatio(ctx, activeBreakPoint, current);

                if (adjustmentRatio < -1 || (
                        ctx.getItems().get(current.getIndex()).getType() == ItemType.PENALTY
                                && ((Penalty) ctx.getItems().get(current.getIndex())).isMandatoryLineBreak())
                ) {
                    deactivateLater.add(activeBreakPoint);
                }

                if (adjustmentRatio >= -1 && adjustmentRatio <= config.getTolerance()) {
                    double demerits = computeDemerits(ctx, current, activeBreakPoint, adjustmentRatio);
                    LineFit lineFit = getLineFit(adjustmentRatio);

                    // Add demerits if two consecutive lines have different fitness classes
                    if (Math.abs(current.getLineFit().getFitnessClass() - activeBreakPoint.getLineFit().getFitnessClass()) > 1) {
                        demerits += config.getFitnessDemerit();
                    }

                    // Save the current break as a potential break
                    current.setLineNumber(activeBreakPoint.getLineNumber() + 1);
                    current.setLineFit(lineFit);
                    current.setDemerits(demerits);
                    current.setPrevious(activeBreakPoint);

                    activateLater.add(current);
                }
            }

            for (BreakPoint toDeactivate : deactivateLater) {
                deactivateActiveBreakPoint(ctx, toDeactivate);
            }
            deactivateLater.clear();

            for (BreakPoint toActivate : activateLater) {
                activateBreakPoint(ctx, toActivate);
            }
            activateLater.clear();
        }

        if (ctx.getActiveBreakPoints().isEmpty()) {
            throw new CouldNotFindFeasibleSolutionException();
        }

        List<BreakPoint> result = new ArrayList<>();

        BreakPoint current = getLastBreakPoint(ctx);
        while (current != null) {
            // Don't add the first break point we introduce to represent the beginning of the paragraph with index = 0
            if (current.getIndex() != 0) {
                result.add(current);
            }

            current = current.getPrevious();
        }

        // We need to reverse the list as it is in the wrong order (last break point to first)
        Collections.reverse(result);

        return new LineBreakingResult(result, ctx);
    }

    /**
     * Get the last break point to build the final list of break points with.
     *
     * @param ctx to get the active break points from
     * @return the last break point
     */
    private BreakPoint getLastBreakPoint(LineBreakingContext ctx) {
        BreakPoint lastBreakPoint = getActiveBreakPointWithFewestDemerits(ctx);

        if (config.getLooseness() != 0) {
            double bestLineDiff = 0;
            double demerits = Double.POSITIVE_INFINITY;
            BreakPoint newLastBreakPoint = lastBreakPoint;

            for (BreakPoint activeBreakPoint : ctx.getActiveBreakPoints()) {
                int lineDiff = activeBreakPoint.getLineNumber() - lastBreakPoint.getLineNumber();

                if ((lineDiff >= config.getLooseness() && lineDiff < bestLineDiff)
                        || (lineDiff > bestLineDiff && lineDiff <= config.getLooseness())) {
                    bestLineDiff = lineDiff;
                    demerits = activeBreakPoint.getDemerits();
                    newLastBreakPoint = activeBreakPoint;
                } else if (lineDiff == bestLineDiff && activeBreakPoint.getDemerits() < demerits) {
                    demerits = activeBreakPoint.getDemerits();
                    newLastBreakPoint = activeBreakPoint;
                }
            }

            lastBreakPoint = newLastBreakPoint;
        }

        return lastBreakPoint;
    }

    /**
     * Get the active break point with fewest demerits.
     *
     * @param ctx to get active break points of
     * @return the active break point with fewest demerits
     */
    private BreakPoint getActiveBreakPointWithFewestDemerits(LineBreakingContext ctx) {
        BreakPoint currentMin = null;

        for (BreakPoint breakPoint : ctx.getActiveBreakPoints()) {
            if (currentMin == null) {
                currentMin = breakPoint;
            } else if (breakPoint.getDemerits() < currentMin.getDemerits()) {
                currentMin = breakPoint;
            }
        }

        return currentMin;
    }

    /**
     * Compute the demerits for the passed current and active break point.
     *
     * @param ctx              context the algorithm is operating upon
     * @param current          the current break point
     * @param activeBreakPoint the currently active break point
     * @param adjustmentRatio  the calculated adjustment ratio
     * @return demerits
     */
    private double computeDemerits(LineBreakingContext ctx, BreakPoint current, BreakPoint activeBreakPoint, double adjustmentRatio) {
        Item currentItem = ctx.getItems().get(current.getIndex());
        Item activeItem = ctx.getItems().get(activeBreakPoint.getIndex());

        double penalty = currentItem.getPenalty();

        double demerits;
        double demeritsBase = 1 + 100 * Math.pow(Math.abs(adjustmentRatio), 3);
        if (penalty >= 0) {
            demerits = Math.pow(demeritsBase + penalty, 2);
        } else if (penalty > Penalty.MIN_PENALTY) {
            demerits = Math.pow(demeritsBase, 2) - Math.pow(penalty, 2);
        } else {
            demerits = Math.pow(demeritsBase, 2);
        }

        boolean areBothBreakPointsFlagged = currentItem.isFlagged() && activeItem.isFlagged();
        if (areBothBreakPointsFlagged) {
            return demerits + config.getFlaggedDemerit(); // Add a "penalty" because we don't want two consecutive flagged breaks
        } else {
            return demerits;
        }
    }

    /**
     * Get the line fit for the passed adjustment ratio.
     *
     * @param adjustmentRatio to get line fit for
     * @return line fit
     */
    private LineFit getLineFit(double adjustmentRatio) {
        if (adjustmentRatio < -0.5) {
            return LineFit.TIGHT;
        } else if (adjustmentRatio <= 0.5) {
            return LineFit.NORMAL;
        } else if (adjustmentRatio <= 1) {
            return LineFit.LOOSE;
        } else {
            return LineFit.VERY_LOOSE;
        }
    }

    /**
     * Deactivate the passed active break point.
     *
     * @param ctx        context used during the algorithm
     * @param breakPoint to deactivate
     */
    private void deactivateActiveBreakPoint(LineBreakingContext ctx, BreakPoint breakPoint) {
        ctx.getActiveBreakPoints().remove(breakPoint);
    }

    /**
     * Activate the passed break point.
     *
     * @param ctx        context used during the algorithm
     * @param breakPoint to activate
     */
    private void activateBreakPoint(LineBreakingContext ctx, BreakPoint breakPoint) {
        // Find the index in the active break points list where the line is equal or greater to the passed break points
        int index = 0;
        int len = ctx.getActiveBreakPoints().size();
        while (index < len && ctx.getActiveBreakPoints().get(index).getLineNumber() < breakPoint.getLineNumber()) {
            index++;
        }

        // Checking whether the break point to insert is unique in terms of line number, position and fitness
        int inLineIndex = index;
        while (inLineIndex < len && ctx.getActiveBreakPoints().get(inLineIndex).getLineNumber() == breakPoint.getLineNumber()) {
            BreakPoint other = ctx.getActiveBreakPoints().get(inLineIndex);

            if (other.getIndex() == breakPoint.getIndex()
                    && other.getLineFit() == breakPoint.getLineFit()) {
                return; // Already having the active break point
            }

            inLineIndex++;
        }

        // Insert into the active break points list
        if (index == len) {
            ctx.getActiveBreakPoints().add(breakPoint);
        } else {
            ctx.getActiveBreakPoints().set(index, breakPoint);
        }
    }

    /**
     * Check if the item at the passed index is a legal break point.
     *
     * @param ctx   line breaking context to operate upon
     * @param index to check at
     * @return whether legal break point
     */
    private boolean isLegalBreakPointAt(LineBreakingContext ctx, int index) {
        Item item = ctx.getItems().get(index);

        return switch (item.getType()) {
            case GLUE -> {
                boolean hasPreviousItem = index > 0;
                if (hasPreviousItem) {
                    Item previous = ctx.getItems().get(index - 1);
                    if (previous.getType() == ItemType.BOX) {
                        yield true;
                    }
                }

                yield false;
            }
            case PENALTY -> {
                Penalty penalty = (Penalty) item;
                boolean isPossibleLineBreak = !penalty.isImpossibleLineBreak();
                yield isPossibleLineBreak;
            }
            default -> false;
        };
    }

    /**
     * Compute the adjustment ratio from the given starting break point
     * to the passed ending break point that form a potential line.
     * <p>
     * The adjustment ratio gives the ratio needed to stretch or shrink the lines width to fit
     * the required line width.
     *
     * @param ctx   the line breaking context to operate upon
     * @param start the starting break point
     * @param end   the ending break point
     * @return the adjustment ratio
     */
    private double computeAdjustmentRatio(LineBreakingContext ctx, BreakPoint start, BreakPoint end) {
        double lineWidth = ctx.getTotalWidth(end.getIndex()) - ctx.getTotalWidth(start.getIndex());

        // Adding penalty width (if any) for example for a hyphen character '-'.
        Item itemOfEnd = ctx.getItems().get(end.getIndex());
        if (itemOfEnd.getType() == ItemType.PENALTY) {
            lineWidth += itemOfEnd.getWidth();
        }

        int lineNumber = start.getLineNumber() + 1;
        double requiredLineWidth = ctx.getLineWidth(lineNumber);

        if (lineWidth < requiredLineWidth) {
            // Line width is too small. We need to stretch it!
            double lineStretchability = ctx.getTotalStretch(end.getIndex()) - ctx.getTotalStretch(start.getIndex());

            return lineStretchability > 0 ? (requiredLineWidth - lineWidth) / lineStretchability : Double.POSITIVE_INFINITY;
        } else if (lineWidth > requiredLineWidth) {
            // Line width is too big. We need to shrink it!
            double lineShrinkability = ctx.getTotalShrink(end.getIndex()) - ctx.getTotalShrink(start.getIndex());

            return lineShrinkability > 0 ? (requiredLineWidth - lineWidth) / lineShrinkability : Double.POSITIVE_INFINITY;
        } else {
            return 0; // Line width fits the required line width exactly!
        }
    }

    /**
     * The result of the line breaking algorithm method.
     */
    private static class LineBreakingResult {

        /**
         * The list of found break points.
         */
        private final List<BreakPoint> breakPoints;

        /**
         * The line breaking context used during the algorithm.
         */
        private final LineBreakingContext context;

        public LineBreakingResult(List<BreakPoint> breakPoints, LineBreakingContext context) {
            this.breakPoints = breakPoints;
            this.context = context;
        }

        public List<BreakPoint> getBreakPoints() {
            return breakPoints;
        }

        public LineBreakingContext getContext() {
            return context;
        }

    }

    /**
     * Metrics about a line.
     */
    private static class LineMetrics {

        /**
         * Minimum width of the line.
         */
        private final double minWidth;

        /**
         * Count of white spaces in the line.
         */
        private final int whiteSpaces;

        public LineMetrics(double minWidth, int whiteSpaces) {
            this.minWidth = minWidth;
            this.whiteSpaces = whiteSpaces;
        }

        /**
         * Get the minimum width the line needs.
         *
         * @return minimum width
         */
        public double getMinWidth() {
            return minWidth;
        }

        /**
         * Get the white space count in the line.
         *
         * @return white space count
         */
        public int getWhiteSpaces() {
            return whiteSpaces;
        }

    }

}
