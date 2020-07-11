package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.style.model.style.text.TextAlignment;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.KnuthPlassAlgorithm;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.exception.CouldNotFindFeasibleSolutionException;
import de.be.thaw.typeset.knuthplass.item.Item;
import de.be.thaw.typeset.knuthplass.item.ItemType;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.EnumerationItemStartBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntToDoubleFunction;

/**
 * Handler dealing with type setting text paragraphs.
 */
public class TextParagraphHandler implements ParagraphTypesetHandler {

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.TEXT;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
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
        ctx.getPositionContext().increaseY(insetsStyle.getTop());
        ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft());

        // Check if we have a floating element nearby
        if (ctx.getFloatConfig().getFloatUntilY() > ctx.getPositionContext().getY()) {
            // Calculate count of lines that are affected by the floating paragraph
            double diff = ctx.getFloatConfig().getFloatUntilY() - ctx.getPositionContext().getY();
            int lineCount = (int) Math.ceil(diff / lineHeight);

            // Add float indent (if any)
            ctx.getPositionContext().increaseX(ctx.getFloatConfig().getFloatIndent());

            // Set a line width supplier that adjusts the line width for the affected lines
            IntToDoubleFunction oldLineWidthSupplier = textParagraph.getLineWidthSupplier();
            textParagraph.setLineWidthSupplier(lineNumber -> {
                double oldLineWidth = oldLineWidthSupplier != null
                        ? oldLineWidthSupplier.applyAsDouble(lineNumber)
                        : textParagraph.getDefaultLineWidth();

                if (lineNumber <= lineCount) {
                    return oldLineWidth - ctx.getFloatConfig().getFloatWidth();
                } else {
                    return oldLineWidth;
                }
            });
        }

        // Find the break points in the text paragraph to split the paragraph into lines with later
        KnuthPlassAlgorithm.LineBreakingResult result = findBreakPoints(textParagraph, ctx);

        // Split the text paragraph into lines using the previously computed break points
        List<List<Item>> lines = splitParagraphIntoLines(textParagraph, result);

        // Lay out the found lines
        double indent = 0; // Indent of the paragraph (if any), set for example for enumerations.
        for (int i = 0; i < lines.size(); i++) {
            List<Item> line = lines.get(i);

            double availableHeight = (ctx.getConfig().getPageSize().getHeight() - ctx.getConfig().getPageInsets().getBottom()) - ctx.getPositionContext().getY();
            if (availableHeight < lineHeight) {
                // Not enough space for this line left on the current page -> Create next page
                ctx.pushPage();
            }

            // Calculating some metrics describing the line in more detail
            double lineWidth = result.getContext().getLineWidth(i + 1);
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
                        : ctx.getConfig().getFontDetailsSupplier().getSpaceWidth(paragraph.getNode());
            } catch (Exception e) {
                throw new TypeSettingException("Could not determine space character width", e);
            }

            // Deal with text alignments other than left (the default).
            if (!justifyLine) {
                double restWidth = lineWidth - lineMetrics.getMinWidth() - lineMetrics.getWhiteSpaces() * spaceWidth;

                if (alignment == TextAlignment.RIGHT) {
                    ctx.getPositionContext().increaseX(restWidth);
                } else if (alignment == TextAlignment.CENTER) {
                    ctx.getPositionContext().increaseX(restWidth / 2);
                }
            }

            for (Item item : line) {
                // Check if the item indicates an enumeration item start
                if (item instanceof EnumerationItemStartBox) {
                    indent = ((EnumerationItemStartBox) item).getIndent();
                    ctx.getPositionContext().increaseX(indent - item.getWidth());
                }

                if (item instanceof TextBox) {
                    ctx.pushPageElement(new TextElement(
                            ((TextBox) item).getText(),
                            ((TextBox) item).getFontSize(),
                            ((TextBox) item).getKerningAdjustments(),
                            ((TextBox) item).getNode(),
                            new Size(item.getWidth(), lineHeight),
                            new Position(ctx.getPositionContext().getX(), ctx.getPositionContext().getY())
                    ));

                    ctx.getPositionContext().increaseX(item.getWidth());
                } else if (item instanceof Penalty) {
                    if (item.isFlagged() && item.getWidth() > 0) {
                        // Is a hyphen because the width is bigger than 0 -> add the '-'-character
                        ctx.pushPageElement(new TextElement(
                                "-",
                                1.0,
                                new double[]{0},
                                ((Penalty) item).getNode(),
                                new Size(item.getWidth(), lineHeight),
                                new Position(ctx.getPositionContext().getX(), ctx.getPositionContext().getY())
                        ));

                        ctx.getPositionContext().increaseX(item.getWidth());
                    }
                } else if (item instanceof Glue) {
                    if (item.getWidth() > 0) { // Is a white space
                        ctx.getPositionContext().increaseX(spaceWidth);
                    }
                } else {
                    ctx.getPositionContext().increaseX(item.getWidth());
                }
            }

            ctx.getPositionContext().increaseY(lineHeight);
            ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + indent);

            boolean isFloating = ctx.getFloatConfig().getFloatUntilY() > ctx.getPositionContext().getY();
            if (isFloating) {
                ctx.getPositionContext().increaseX(ctx.getFloatConfig().getFloatIndent());
            }
        }

        ctx.getPositionContext().increaseY(insetsStyle.getBottom());
    }

    /**
     * Find break points for the passed paragraph.
     *
     * @param textParagraph to find break points for
     * @param ctx           the type setting context
     * @return the found break points
     * @throws TypeSettingException in case the algorithm could not find a feasible solution
     */
    private KnuthPlassAlgorithm.LineBreakingResult findBreakPoints(TextParagraph textParagraph, TypeSettingContext ctx) throws TypeSettingException {
        // Do the line breaking. Try several quality levels in case it does not work.
        for (int quality = 0; quality < 10; quality++) {
            KnuthPlassAlgorithm algorithm = new KnuthPlassAlgorithm(ctx.getConfig(), quality);

            try {
                return algorithm.findBreakPoints(textParagraph);
            } catch (CouldNotFindFeasibleSolutionException e) {
                if (quality == 9) {
                    throw new TypeSettingException("Typesetting failed because the line breaking algorithm could not find a feasible solution", e);
                }
            }
        }

        throw new TypeSettingException();
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
    private List<List<Item>> splitParagraphIntoLines(TextParagraph paragraph, KnuthPlassAlgorithm.LineBreakingResult lineBreakingResult) {
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