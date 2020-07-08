package de.be.thaw.typeset.knuthplass;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.style.model.style.text.TextAlignment;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.converter.KnuthPlassConverter;
import de.be.thaw.typeset.knuthplass.exception.CouldNotFindFeasibleSolutionException;
import de.be.thaw.typeset.knuthplass.item.Item;
import de.be.thaw.typeset.knuthplass.item.ItemType;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.EnumerationItemStartBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.image.ImageParagraph;
import de.be.thaw.typeset.knuthplass.util.BreakPoint;
import de.be.thaw.typeset.knuthplass.util.LineBreakingContext;
import de.be.thaw.typeset.knuthplass.util.LineFit;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.page.impl.ImageElement;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.IntToDoubleFunction;

/**
 * Implementation of the Knuth-Plass line breaking algorithm.
 */
public class KnuthPlassTypeSetter implements TypeSetter {

    /**
     * Configuration of the
     */
    private final KnuthPlassTypeSettingConfig config;

    /**
     * List of lists of consecutive paragraphs to type set.
     */
    private List<List<Paragraph>> paragraphs;

    /**
     * The currently typeset pages.
     */
    private List<Page> pages;

    /**
     * Elements of the current page.
     */
    private List<Element> currentPageElements;

    /**
     * Quality level used to let the line breaking algorithm succeed eventually when
     * it cannot find a solution with best quality.
     */
    private int lineBreakingQuality = 0;

    public KnuthPlassTypeSetter(KnuthPlassTypeSettingConfig config) {
        this.config = config;
    }

    @Override
    public List<Page> typeset(Document document) throws TypeSettingException {
        try {
            paragraphs = new KnuthPlassConverter(config).convert(document);
        } catch (DocumentConversionException e) {
            throw new TypeSettingException("Could not convert the document into the Knuth-Plass algorithm format", e);
        }

        pages = new ArrayList<>();
        currentPageElements = new ArrayList<>();

        // Current x and y offsets
        double y = config.getPageInsets().getTop();

        for (int consecutiveParagraphsIndex = 0; consecutiveParagraphsIndex < paragraphs.size(); consecutiveParagraphsIndex++) {
            List<Paragraph> consecutiveParagraphs = paragraphs.get(consecutiveParagraphsIndex);

            double floatUntilY = -1; // Until that y coordinate we have a floading element nearby
            double floatIndent = 0; // Indent due to floating element
            double floatWidth = 0; // Set when the width of a paragraph is reduced due to floating elements
            for (Paragraph paragraph : consecutiveParagraphs) {
                // TODO Typesetting handler for each paragraph type!
                if (paragraph instanceof TextParagraph) {
                    TextParagraph textParagraph = (TextParagraph) paragraph;

                    // Fetch some paragraph styles
                    final double lineHeight = getLineHeightForNode(paragraph.getNode());
                    final InsetsStyle insetsStyle = paragraph.getNode().getStyle().getStyleAttribute(
                            StyleType.INSETS,
                            style -> Optional.ofNullable((InsetsStyle) style)
                    ).orElseThrow();
                    final TextAlignment alignment = paragraph.getNode().getStyle().getStyleAttribute(
                            StyleType.TEXT,
                            style -> Optional.ofNullable(((TextStyle) style).getAlignment())
                    ).orElse(TextAlignment.LEFT);
                    final boolean justify = paragraph.getNode().getStyle().getStyleAttribute(
                            StyleType.TEXT,
                            style -> Optional.ofNullable(((TextStyle) style).getJustify())
                    ).orElse(true);

                    // Set up the initial position of the paragraph
                    y += insetsStyle.getTop();
                    double x = config.getPageInsets().getLeft();

                    // Check if we have a floating element nearby
                    if (floatUntilY > y) {
                        double diff = floatUntilY - y;
                        int lineCount = (int) Math.ceil(diff / lineHeight);

                        x += floatIndent;

                        IntToDoubleFunction oldLineWidthSupplier = textParagraph.getLineWidthSupplier();

                        double finalFloatWidth = floatWidth;
                        textParagraph.setLineWidthSupplier(lineNumber -> {
                            double oldLineWidth = oldLineWidthSupplier != null ? oldLineWidthSupplier.applyAsDouble(lineNumber) : textParagraph.getDefaultLineWidth();
                            if (lineNumber <= lineCount) {
                                return oldLineWidth - finalFloatWidth;
                            } else {
                                return oldLineWidth;
                            }
                        });
                    }

                    // Do the line breaking. Try several quality levels in case it does not work.
                    LineBreakingResult result = null;
                    for (int quality = 0; quality < 10; quality++) {
                        lineBreakingQuality = quality;

                        try {
                            result = findBreakPoints(textParagraph);
                            break;
                        } catch (CouldNotFindFeasibleSolutionException e) {
                            if (quality == 9) {
                                throw new TypeSettingException("Typesetting failed because the line breaking algorithm could not find a feasible solution", e);
                            }
                        }
                    }

                    List<List<Item>> lines = splitParagraphIntoLines(textParagraph, result);

                    // Lay out the individual lines
                    double indent = 0; // Indent of the paragraph (if any), set for example for enumerations.
                    for (int i = 0; i < lines.size(); i++) {
                        double availableHeight = (config.getPageSize().getHeight() - config.getPageInsets().getBottom()) - y;
                        if (availableHeight < lineHeight) {
                            // Create next page
                            pages.add(new Page(pages.size() + 1, config.getPageSize(), config.getPageInsets(), currentPageElements));
                            currentPageElements = new ArrayList<>();
                            y = config.getPageInsets().getTop();
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

                        // Last item is glue with width 0 -> indicates explicit line break
                        Item last = line.get(line.size() - 1);
                        boolean isExplicitLineBreakInLine = last.getType() == ItemType.GLUE && last.getWidth() == 0 && last.getStretchability() > 0;

                        boolean isLastLine = i == lines.size() - 1;
                        boolean justifyLine = justify && !isExplicitLineBreakInLine && !isLastLine;

                        // Determine the space width
                        double spaceWidth;
                        try {
                            spaceWidth = justifyLine
                                    ? getJustifiedLineSpaceWidth(lineMetrics, lineWidth)
                                    : config.getFontDetailsSupplier().getSpaceWidth(paragraph.getNode());
                        } catch (Exception e) {
                            throw new TypeSettingException("Could not determine space character width", e);
                        }

                        // Deal with text alignments other than left (the default).
                        if (!justifyLine) {
                            double restWidth = lineWidth - lineMetrics.getMinWidth() - lineMetrics.getWhiteSpaces() * spaceWidth;

                            if (alignment == TextAlignment.RIGHT) {
                                x += restWidth;
                            } else if (alignment == TextAlignment.CENTER) {
                                x += restWidth / 2;
                            }
                        }

                        for (Item item : line) {
                            // Check if the item indicates an enumeration item start
                            if (item instanceof EnumerationItemStartBox) {
                                indent = ((EnumerationItemStartBox) item).getIndent();
                                x += indent - item.getWidth();
                            }

                            if (item instanceof TextBox) {
                                currentPageElements.add(new TextElement(
                                        ((TextBox) item).getText(),
                                        ((TextBox) item).getNode(),
                                        new Size(item.getWidth(), lineHeight),
                                        new Position(x, y)
                                ));

                                x += item.getWidth();
                            } else if (item instanceof Penalty) {
                                if (item.isFlagged() && item.getWidth() > 0) {
                                    currentPageElements.add(new TextElement(
                                            "-",
                                            ((Penalty) item).getNode(),
                                            new Size(item.getWidth(), lineHeight),
                                            new Position(x, y)
                                    ));

                                    x += item.getWidth();
                                }
                            } else if (item instanceof Glue) {
                                if (item.getWidth() > 0) {
                                    x += spaceWidth;
                                }
                            } else {
                                x += item.getWidth();
                            }
                        }

                        y += lineHeight;
                        x = config.getPageInsets().getLeft() + indent;

                        boolean isFloating = floatUntilY > y;
                        if (isFloating) {
                            x += floatIndent;
                        }
                    }

                    y += insetsStyle.getBottom();
                } else if (paragraph instanceof ImageParagraph) {
                    ImageParagraph imageParagraph = (ImageParagraph) paragraph;

                    final InsetsStyle insetsStyle = paragraph.getNode().getStyle().getStyleAttribute(
                            StyleType.INSETS,
                            style -> Optional.ofNullable((InsetsStyle) style)
                    ).orElseThrow();

                    y += insetsStyle.getTop();

                    double width = imageParagraph.getLineWidth(1);

                    double ratio = imageParagraph.getSrc().getSize().getWidth() / imageParagraph.getSrc().getSize().getHeight();
                    double height = width / ratio;

                    double maxWidth = config.getPageSize().getWidth() - (config.getPageInsets().getLeft() + config.getPageInsets().getRight()) - (insetsStyle.getLeft() + insetsStyle.getRight());
                    double x = config.getPageInsets().getLeft();
                    if (imageParagraph.getAlignment() == TextAlignment.CENTER) {
                        x += (maxWidth - width) / 2;
                    } else if (imageParagraph.getAlignment() == TextAlignment.RIGHT) {
                        x += maxWidth - width;
                    }

                    x += insetsStyle.getLeft();

                    currentPageElements.add(new ImageElement(
                            imageParagraph.getSrc(),
                            imageParagraph.getNode(),
                            new Size(width, height),
                            new Position(x, y)
                    ));

                    if (imageParagraph.isFloating() && imageParagraph.getAlignment() != TextAlignment.CENTER) {
                        floatUntilY += y + height + insetsStyle.getBottom();
                        floatWidth = width + insetsStyle.getLeft() + insetsStyle.getRight();
                        floatIndent = imageParagraph.getAlignment() == TextAlignment.LEFT ? floatWidth : 0;
                    } else {
                        y += height + insetsStyle.getBottom();
                    }
                }
            }

            // Push the current page.
            pages.add(new Page(pages.size() + 1, config.getPageSize(), config.getPageInsets(), currentPageElements));

            // Create next page (explicit page break) when this is not the last list of consecutive paragraphs
            if (consecutiveParagraphsIndex != paragraphs.size() - 1) {
                currentPageElements = new ArrayList<>();
                y = config.getPageInsets().getTop();
            }
        }
        return pages;
    }

    /**
     * Get the font size for the passed node.
     *
     * @param node to get font size for
     * @return font size
     */
    private double getLineHeightForNode(DocumentNode node) {
        return node.getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineHeight())
        ).orElse(node.getStyle().getStyleAttribute(
                StyleType.FONT,
                style -> Optional.ofNullable(((FontStyle) style).getSize())
        ).orElseThrow());
    }

    /**
     * Split the passed paragraph using the given line breaking algorithm result into lines.
     *
     * @param paragraph          to split into lines
     * @param lineBreakingResult to split the paragraph into lines with
     * @return lines
     */
    private List<List<Item>> splitParagraphIntoLines(TextParagraph paragraph, LineBreakingResult lineBreakingResult) {
        List<List<Item>> lines = new ArrayList<>();

        List<Item> currentLine = new ArrayList<>();
        lines.add(currentLine);

        int nextBreakPointIndex = 0;
        int len = paragraph.items().size();
        for (int i = 0; i < len; i++) {
            Item item = paragraph.items().get(i);

            boolean isBreakPoint = lineBreakingResult.getBreakPoints().size() > nextBreakPointIndex
                    && i == lineBreakingResult.getBreakPoints().get(nextBreakPointIndex).getIndex();

            if (item.getType() == ItemType.GLUE) {
                // Skip leading, trailing and consecutive glues (except if they indicate explicit line breaks)
                if (currentLine.size() > 0) {
                    Item previous = currentLine.get(currentLine.size() - 1);

                    boolean indicatesExplicitLineBreak = item.getWidth() == 0 && item.getStretchability() > 0;
                    boolean add = !isBreakPoint && (previous.getType() != ItemType.GLUE || indicatesExplicitLineBreak);
                    if (add) {
                        currentLine.add(item);
                    }
                }
            } else if (item.getType() == ItemType.PENALTY) {
                if (item.getWidth() > 0 && item.isFlagged()) {
                    // Only allow trailing penalties
                    if (isBreakPoint) {
                        currentLine.add(item);
                    }
                } else {
                    currentLine.add(item);
                }
            } else {
                currentLine.add(item);
            }

            if (isBreakPoint) {
                nextBreakPointIndex++;

                // Push new line
                currentLine = new ArrayList<>();
                lines.add(currentLine);
            }
        }

        // Remove the last empty line
        if (lines.get(lines.size() - 1).isEmpty()) {
            lines.remove(lines.size() - 1);
        }

        return lines;
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
     * @return the found break points
     * @throws CouldNotFindFeasibleSolutionException in case the algorithm could not find a feasible solution
     */
    private LineBreakingResult findBreakPoints(TextParagraph paragraph) throws CouldNotFindFeasibleSolutionException {
        LineBreakingContext ctx = new LineBreakingContext(paragraph, lineBreakingQuality);

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

                if ((lineDiff >= config.getLooseness() + lineBreakingQuality && lineDiff < bestLineDiff)
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
