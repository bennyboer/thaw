package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.KnuthPlassAlgorithm;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.exception.CouldNotFindFeasibleSolutionException;
import de.be.thaw.typeset.knuthplass.item.Item;
import de.be.thaw.typeset.knuthplass.item.ItemType;
import de.be.thaw.typeset.knuthplass.item.impl.Glue;
import de.be.thaw.typeset.knuthplass.item.impl.Penalty;
import de.be.thaw.typeset.knuthplass.item.impl.box.EnumerationItemStartBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.FootNoteBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.MathBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.PageNumberPlaceholderBox;
import de.be.thaw.typeset.knuthplass.item.impl.box.TextBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.impl.MathExpressionElement;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntToDoubleFunction;

/**
 * Handler dealing with type setting text paragraphs.
 */
public class TextParagraphHandler implements ParagraphTypesetHandler {

    /**
     * The worst quality the Knuth-Plass algorithm is allowed to output in.
     */
    private static final int WORST_QUALITY = 10;

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
        final HorizontalAlignment alignment = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getAlignment())
        ).orElse(HorizontalAlignment.LEFT);
        final boolean justify = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getJustify())
        ).orElse(true);
        final boolean showLineNumbers = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).isShowLineNumbers())
        ).orElse(false);
        final String lineNumberFontFamily = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineNumberFontFamily())
        ).orElse("Cambria");
        final Double lineNumberFontSize = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineNumberFontSize())
        ).orElse(7.0);
        final ColorStyle lineNumberColor = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineNumberColor())
        ).orElse(null);

        // Calculate some metrics
        double baseline;
        try {
            baseline = ctx.getConfig().getFontDetailsSupplier().measureString(textParagraph.getNode(), -1, "M").getBaseline();
        } catch (Exception e) {
            throw new TypeSettingException(e);
        }

        // Set up the initial position of the paragraph
        ctx.getPositionContext().increaseY(insetsStyle.getTop());
        ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + insetsStyle.getLeft());

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

        // Reduce line width supplier line width by the paragraphs left and right indents
        if (insetsStyle.getLeft() != 0 || insetsStyle.getRight() != 0) {
            final double reduceBy = insetsStyle.getLeft() + insetsStyle.getRight();
            IntToDoubleFunction oldLineWidthSupplier = textParagraph.getLineWidthSupplier();
            textParagraph.setLineWidthSupplier(lineNumber -> {
                if (oldLineWidthSupplier != null) {
                    return oldLineWidthSupplier.applyAsDouble(lineNumber) - reduceBy;
                } else {
                    return textParagraph.getDefaultLineWidth() - reduceBy;
                }
            });
        }

        // Find the break points in the text paragraph to split the paragraph into lines with later
        KnuthPlassAlgorithm.LineBreakingResult result = findBreakPoints(textParagraph, ctx);

        // Split the text paragraph into lines using the previously computed break points
        List<List<Item>> lines = splitParagraphIntoLines(textParagraph, result);

        // Lay out the found lines
        double indent = insetsStyle.getLeft(); // Indent of the paragraph (if any), set for example for enumerations.
        for (int i = 0; i < lines.size(); i++) {
            List<Item> line = lines.get(i);

            // Check if line contains foot note -> must fit on the current page!
            for (Item item : line) {
                if (item instanceof FootNoteBox) {
                    // Push foot note to be included on the page
                    ctx.pushFootNote(((FootNoteBox) item).getNode());

                    if (ctx.getAvailableHeight() < lineHeight) {
                        // Create new page first as foot note and foot note reference do not fit on the same page anymore
                        List<Element> footNoteElements = ctx.popFootNote(); // Pop the last foot note again from the current page
                        ctx.pushPage(); // Push the next page
                        ctx.pushFootNote(footNoteElements);
                    }
                }
            }

            // Check if there is enough space for the line
            double availableHeight = ctx.getAvailableHeight();
            if (availableHeight < lineHeight) {
                // Not enough space for this line left on the current page -> Create next page
                ctx.pushPage();
            }

            // Calculating some metrics describing the line in more detail
            double lineWidth = result.getContext().getLineWidth(i + 1);
            LineMetrics lineMetrics = calculateLineMetrics(line);

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

                if (alignment == HorizontalAlignment.RIGHT) {
                    ctx.getPositionContext().increaseX(restWidth);
                } else if (alignment == HorizontalAlignment.CENTER) {
                    ctx.getPositionContext().increaseX(restWidth / 2);
                }
            }

            int currentLineNumber = ctx.increaseAndGetLineNumber();

            // Show the current line number in front of the text (if showLineNumbers is true).
            if (showLineNumbers) {
                // Stringify line number
                String lineNumberStr = String.valueOf(currentLineNumber);

                // Create dummy document node representing the line number string
                Map<StyleType, Style> lineNumberStyles = new HashMap<>();
                lineNumberStyles.put(StyleType.FONT, new FontStyle(lineNumberFontFamily, FontVariant.MONOSPACE, lineNumberFontSize, lineNumberColor, null, null));
                DocumentNodeStyle lineNumberNodeStyle = new DocumentNodeStyle(paragraph.getNode().getStyle(), lineNumberStyles);
                DocumentNode lineNumberDocumentNode = new DocumentNode(new TextNode(lineNumberStr, null), paragraph.getNode(), lineNumberNodeStyle);

                // Measure line number string
                FontDetailsSupplier.StringMetrics lineNumberStrMetrics;
                try {
                    lineNumberStrMetrics = ctx.getConfig().getFontDetailsSupplier().measureString(lineNumberDocumentNode, -1, lineNumberStr);
                } catch (Exception e) {
                    throw new TypeSettingException(e);
                }

                ctx.pushPageElement(new TextElement(
                        lineNumberStr,
                        lineNumberStrMetrics,
                        lineNumberDocumentNode,
                        ctx.getCurrentPageNumber(),
                        baseline,
                        new Size(lineNumberStrMetrics.getWidth(), lineHeight),
                        new Position(ctx.getConfig().getPageInsets().getLeft() - lineNumberStrMetrics.getWidth() - 10.0, ctx.getPositionContext().getY())
                ));
            }

            for (Item item : line) {
                // Check if the item indicates an enumeration item start
                if (item instanceof EnumerationItemStartBox) {
                    indent += ((EnumerationItemStartBox) item).getIndent();
                    ctx.getPositionContext().increaseX(indent - item.getWidth());
                }

                if (item instanceof TextBox) {
                    TextBox tb = (TextBox) item;

                    if (item instanceof PageNumberPlaceholderBox) {
                        // Replace text with the actual page number we are currently at
                        String text = String.valueOf(ctx.getCurrentPageNumber());

                        // Measure the page number string and set it on the text box item
                        try {
                            var metrics = ctx.getConfig().getFontDetailsSupplier().measureString(tb.getNode(), -1, text);
                            ((PageNumberPlaceholderBox) item).set(text, metrics.getWidth(), metrics.getKerningAdjustments(), metrics.getFontSize());
                        } catch (Exception e) {
                            throw new TypeSettingException(e);
                        }
                    }

                    ctx.pushPageElement(new TextElement(
                            tb.getText(),
                            tb.getMetrics(),
                            tb.getNode(),
                            ctx.getCurrentPageNumber(),
                            baseline,
                            new Size(item.getWidth(), lineHeight),
                            new Position(ctx.getPositionContext().getX(), ctx.getPositionContext().getY())
                    ));

                    ctx.getPositionContext().increaseX(item.getWidth());
                } else if (item instanceof Penalty) {
                    if (item.isFlagged() && item.getWidth() > 0) {
                        // Is a hyphen because the width is bigger than 0 -> add the '-'-character
                        ctx.pushPageElement(new TextElement(
                                ((Penalty) item).getReplacementString(),
                                ((Penalty) item).getMetrics(),
                                ((Penalty) item).getNode(),
                                ctx.getCurrentPageNumber(),
                                baseline,
                                new Size(item.getWidth(), lineHeight),
                                new Position(ctx.getPositionContext().getX(), ctx.getPositionContext().getY())
                        ));

                        ctx.getPositionContext().increaseX(item.getWidth());
                    }
                } else if (item instanceof Glue) {
                    if (item.getWidth() > 0) { // Is a white space
                        ctx.getPositionContext().increaseX(spaceWidth);
                    }
                } else if (item instanceof MathBox) {
                    MathBox box = (MathBox) item;

                    ctx.pushPageElement(new MathExpressionElement(
                            box.getExpression(),
                            ctx.getCurrentPageNumber(),
                            box.getExpression().getSize(),
                            new Position(ctx.getPositionContext().getX(), ctx.getPositionContext().getY()),
                            box.getNode(),
                            true,
                            baseline
                    ));

                    ctx.getPositionContext().increaseX(item.getWidth());
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
        for (int quality = 0; quality < WORST_QUALITY; quality++) {
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
