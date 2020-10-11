package de.be.thaw.typeset.knuthplass.paragraph.handler.impl.text;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.value.BooleanStyleValue;
import de.be.thaw.style.model.style.value.ColorStyleValue;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.style.model.style.value.FontFamilyStyleValue;
import de.be.thaw.style.model.style.value.FontVariantStyleValue;
import de.be.thaw.style.model.style.value.HorizontalAlignmentStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
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
import de.be.thaw.typeset.knuthplass.util.BreakPoint;
import de.be.thaw.typeset.knuthplass.util.LineBreakingContext;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.impl.MathExpressionElement;
import de.be.thaw.typeset.page.impl.RectangleElement;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.debug.Debug;
import de.be.thaw.util.unit.BaseUnit;
import de.be.thaw.util.unit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handler dealing with type setting text paragraphs.
 */
public class TextParagraphHandler implements ParagraphTypesetHandler {

    /**
     * The worst quality the Knuth-Plass algorithm is allowed to output in.
     */
    private static final int WORST_QUALITY = 5;

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = Logger.getLogger(TextParagraphHandler.class.getSimpleName());

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.TEXT;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        TextParagraph textParagraph = (TextParagraph) paragraph;

        // Fetch some paragraph styles
        Styles styles = paragraph.getNode().getStyles();

        double lineHeight;
        StyleValue lineHeightStyleValue = styles.resolve(StyleType.LINE_HEIGHT).orElseThrow();
        if (lineHeightStyleValue.unit().getBaseUnit() == BaseUnit.UNITARY) {
            // Is relative line-height -> Calculate line height from the font size
            lineHeight = styles.resolve(StyleType.FONT_SIZE)
                    .orElseThrow()
                    .doubleValue(Unit.POINTS) * lineHeightStyleValue.doubleValue(Unit.UNITARY);
        } else {
            lineHeight = lineHeightStyleValue.doubleValue(Unit.POINTS);
        }

        final HorizontalAlignment alignment = styles.resolve(StyleType.TEXT_ALIGN).orElse(new HorizontalAlignmentStyleValue(HorizontalAlignment.LEFT)).horizontalAlignment();
        final boolean justify = styles.resolve(StyleType.TEXT_JUSTIFY).orElse(new BooleanStyleValue(true)).booleanValue();
        final boolean showLineNumbers = styles.resolve(StyleType.SHOW_LINE_NUMBERS).orElse(new BooleanStyleValue(false)).booleanValue();
        final String lineNumberFontFamily = styles.resolve(StyleType.SHOW_LINE_NUMBERS).orElseThrow().value();

        final double lineNumberFontSize = styles.resolve(StyleType.LINE_NUMBER_FONT_SIZE)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        final Color lineNumberColor = styles.resolve(StyleType.LINE_NUMBER_COLOR).orElseThrow().colorValue();

        // Calculate margins
        final double marginTop = styles.resolve(StyleType.MARGIN_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginBottom = styles.resolve(StyleType.MARGIN_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginLeft = styles.resolve(StyleType.MARGIN_LEFT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginRight = styles.resolve(StyleType.MARGIN_RIGHT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        // Calculate paddings
        final double paddingTop = styles.resolve(StyleType.PADDING_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingBottom = styles.resolve(StyleType.PADDING_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingLeft = styles.resolve(StyleType.PADDING_LEFT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingRight = styles.resolve(StyleType.PADDING_RIGHT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        // Calculate some metrics
        double baseline;
        try {
            baseline = ctx.getConfig().getFontDetailsSupplier().measureString(textParagraph.getNode(), -1, "X").getHeight();
        } catch (Exception e) {
            throw new TypeSettingException(e);
        }

        // Save the start y position of the background for later use
        double backgroundStartY = ctx.getPositionContext().getY() + marginTop;
        int firstElementIndex = ctx.getCurrentPageElements().size(); // First element index of the text paragraph

        // Set up the initial position of the paragraph
        ctx.getPositionContext().increaseY(marginTop + paddingTop);
        ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + marginLeft + paddingLeft);

        // Check if we have a floating element nearby
        if (ctx.getFloatConfig().getFloatUntilY() > ctx.getPositionContext().getY()) {
            // Calculate count of lines that are affected by the floating paragraph
            double diff = ctx.getFloatConfig().getFloatUntilY() - ctx.getPositionContext().getY();
            int lineCount = (int) Math.round(Math.min(diff, ctx.getAvailableHeight()) / lineHeight);

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
        if (marginLeft != 0 || marginRight != 0 || paddingLeft != 0 || paddingRight != 0) {
            final double reduceBy = marginLeft + marginRight + paddingLeft + paddingRight;
            IntToDoubleFunction oldLineWidthSupplier = textParagraph.getLineWidthSupplier();
            textParagraph.setLineWidthSupplier(lineNumber -> {
                if (oldLineWidthSupplier != null) {
                    return oldLineWidthSupplier.applyAsDouble(lineNumber) - reduceBy;
                } else {
                    return textParagraph.getDefaultLineWidth() - reduceBy;
                }
            });
        }

        // Since not all items in the text paragraph have the correct width by now (For example the #PAGE# thingy items).
        // We need to set them to a reasonable estimation.
        fixItemWidthWithEstimations(textParagraph, ctx);

        // Find the break points in the text paragraph to split the paragraph into lines with later
        KnuthPlassAlgorithm.LineBreakingResult result = findBreakPoints(textParagraph, ctx);

        // Split the text paragraph into lines using the previously computed break points
        List<List<Item>> lines = splitParagraphIntoLines(textParagraph, result);

        // End-line may contain no boxes -> filter those end-lines
        List<Item> lastLine = lines.get(lines.size() - 1);
        boolean filterLastLine = true;
        for (Item item : lastLine) {
            if (item.getType() == ItemType.BOX) {
                filterLastLine = false;
                break;
            }
        }
        if (filterLastLine) {
            lines.remove(lines.size() - 1);
        }

        // Lay out the found lines
        double indent = marginLeft + paddingLeft; // Indent of the paragraph (if any), set for example for enumerations.
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

                        pushRectangleElementIfNecessary(ctx, backgroundStartY, firstElementIndex, textParagraph); // Push background rectangle element if necessary
                        ctx.pushPage(); // Push the next page
                        backgroundStartY = ctx.getPositionContext().getY();
                        firstElementIndex = ctx.getCurrentPageElements().size();

                        ctx.pushFootNote(footNoteElements);
                    }
                }
            }

            // Check if there is enough space for the line
            double availableHeight = ctx.getAvailableHeight();
            if (availableHeight < lineHeight) {
                // Not enough space for this line left on the current page -> Create next page
                pushRectangleElementIfNecessary(ctx, backgroundStartY, firstElementIndex, textParagraph); // Push background rectangle element if necessary
                ctx.pushPage();
                backgroundStartY = ctx.getPositionContext().getY();
                firstElementIndex = ctx.getCurrentPageElements().size();
            }

            boolean isFloating = ctx.getFloatConfig().getFloatUntilY() > ctx.getPositionContext().getY();
            if (isFloating) {
                ctx.getPositionContext().increaseX(ctx.getFloatConfig().getFloatIndent());
            }

            fixItemWidthForLine(line, ctx);

            // Calculating some metrics describing the line in more detail
            double lineWidth = textParagraph.getLineWidth(i + 1);
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
                Styles lineNumberDocumentNodeStyles = new Styles(paragraph.getNode().getStyles());
                lineNumberDocumentNodeStyles.overrideStyle(StyleType.FONT_FAMILY, new FontFamilyStyleValue(lineNumberFontFamily));
                lineNumberDocumentNodeStyles.overrideStyle(StyleType.FONT_VARIANT, new FontVariantStyleValue(FontVariant.MONOSPACE));
                lineNumberDocumentNodeStyles.overrideStyle(StyleType.FONT_SIZE, new DoubleStyleValue(lineNumberFontSize, Unit.POINTS));
                lineNumberDocumentNodeStyles.overrideStyle(StyleType.COLOR, new ColorStyleValue(lineNumberColor));

                DocumentNode lineNumberDocumentNode = new DocumentNode(new TextNode(lineNumberStr, null), paragraph.getNode(), lineNumberDocumentNodeStyles);

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
        }

        pushRectangleElementIfNecessary(ctx, backgroundStartY, firstElementIndex, textParagraph);
        ctx.getPositionContext().increaseY(marginBottom + paddingBottom);
    }

    /**
     * Push a rectangle element for the background if necessary.
     */
    private void pushRectangleElementIfNecessary(TypeSettingContext ctx, double startY, int startElementIndex, TextParagraph paragraph) {
        // Fetch background and border styles
        Styles styles = paragraph.getNode().getStyles();

        final double marginLeft = styles.resolve(StyleType.MARGIN_LEFT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginRight = styles.resolve(StyleType.MARGIN_RIGHT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingBottom = styles.resolve(StyleType.PADDING_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        Color backgroundColor = styles.resolve(StyleType.BACKGROUND_COLOR).map(StyleValue::colorValue).orElse(new Color(1.0, 1.0, 1.0, 0.0));
        Insets borderWidths = new Insets(
                styles.resolve(StyleType.BORDER_TOP_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.BORDER_RIGHT_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.BORDER_BOTTOM_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.BORDER_LEFT_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0)
        );
        Insets borderRadius = new Insets(
                styles.resolve(StyleType.BORDER_RADIUS_TOP).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.BORDER_RADIUS_RIGHT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.BORDER_RADIUS_BOTTOM).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.BORDER_RADIUS_LEFT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0)
        );
        Color[] borderColors = new Color[]{
                new Color(0.0, 0.0, 0.0),
                new Color(0.0, 0.0, 0.0),
                new Color(0.0, 0.0, 0.0),
                new Color(0.0, 0.0, 0.0)
        };
        styles.resolve(StyleType.BORDER_TOP_COLOR).ifPresent(v -> borderColors[0] = v.colorValue());
        styles.resolve(StyleType.BORDER_RIGHT_COLOR).ifPresent(v -> borderColors[1] = v.colorValue());
        styles.resolve(StyleType.BORDER_BOTTOM_COLOR).ifPresent(v -> borderColors[2] = v.colorValue());
        styles.resolve(StyleType.BORDER_LEFT_COLOR).ifPresent(v -> borderColors[3] = v.colorValue());
        LineStyle[] borderStyles = new LineStyle[]{
                LineStyle.SOLID,
                LineStyle.SOLID,
                LineStyle.SOLID,
                LineStyle.SOLID
        };
        styles.resolve(StyleType.BORDER_TOP_STYLE).ifPresent(v -> borderStyles[0] = LineStyle.valueOf(v.fillStyle().name()));
        styles.resolve(StyleType.BORDER_RIGHT_STYLE).ifPresent(v -> borderStyles[1] = LineStyle.valueOf(v.fillStyle().name()));
        styles.resolve(StyleType.BORDER_BOTTOM_STYLE).ifPresent(v -> borderStyles[2] = LineStyle.valueOf(v.fillStyle().name()));
        styles.resolve(StyleType.BORDER_LEFT_STYLE).ifPresent(v -> borderStyles[3] = LineStyle.valueOf(v.fillStyle().name()));

        Size size = new Size(
                ctx.getConfig().getPageSize().getWidth() - ctx.getConfig().getPageInsets().getLeft() - ctx.getConfig().getPageInsets().getRight() - marginLeft - marginRight,
                ctx.getPositionContext().getY() - startY + paddingBottom
        );
        Position position = new Position(
                ctx.getConfig().getPageInsets().getLeft() + marginLeft,
                startY
        );

        RectangleElement rect = new RectangleElement(ctx.getCurrentPageNumber(), size, position);

        // Check if we have enough settings to push a rectangle
        boolean pushRectangle = false;
        if (backgroundColor.getAlpha() > 0.0) {
            pushRectangle = true;
            rect.setFillColor(backgroundColor);
        }
        if (borderWidths.getTop() > 0 || borderWidths.getRight() > 0 || borderWidths.getBottom() > 0 || borderWidths.getLeft() > 0) {
            pushRectangle = true;
            rect.setBorderWidths(borderWidths);
        }

        if (pushRectangle) {
            rect.setStrokeColors(borderColors);
            rect.setBorderStyles(borderStyles);
            rect.setBorderRadius(borderRadius);

            ctx.getCurrentPageElements().add(startElementIndex, rect);
        }
    }

    /**
     * This method will give items that do not have a fixed width yet an estimated width.
     * An example is the #PAGE# Thingy, that will leave a PageNumberPlaceholderBox in the
     * text paragraph as item. Since we do not yet know what exact page number we are on, we can
     * only give an estimation.
     *
     * @param textParagraph to process
     * @param ctx           the typesetting context
     */
    private void fixItemWidthWithEstimations(TextParagraph textParagraph, TypeSettingContext ctx) throws TypeSettingException {
        for (Item item : textParagraph.items()) {
            if (item instanceof PageNumberPlaceholderBox) {
                PageNumberPlaceholderBox box = (PageNumberPlaceholderBox) item;

                String text = String.valueOf(ctx.getCurrentPageNumber());
                text = "9".repeat(text.length() + 1); // We estimate that the page number will be no wider than the current page number * 10

                // Measure the page number string and set it on the text box item
                try {
                    var metrics = ctx.getConfig().getFontDetailsSupplier().measureString(
                            box.getNode(),
                            -1,
                            text);
                    ((PageNumberPlaceholderBox) item).set(text, metrics.getWidth(), metrics);
                } catch (Exception e) {
                    throw new TypeSettingException(e);
                }
            }
        }
    }

    /**
     * This method will change item widths for items that currently may contain a wrong width.
     * For example the #PAGE# Thingy will produce a PageNumberPlaceholderBox item that
     * does currently only contain an estimation of the actual width.
     * However for only a line we can precisely determine the actual width of a PageNumberPlaceholderBox.
     *
     * @param items to fix estimated values for (if any).
     * @param ctx   the typesetting context
     * @throws TypeSettingException in case the widths could not be fixed properly
     */
    private void fixItemWidthForLine(List<Item> items, TypeSettingContext ctx) throws TypeSettingException {
        for (Item item : items) {
            if (item instanceof PageNumberPlaceholderBox) {
                PageNumberPlaceholderBox box = (PageNumberPlaceholderBox) item;

                String text = String.valueOf(ctx.getCurrentPageNumber());

                // Measure the page number string and set it on the text box item
                try {
                    var metrics = ctx.getConfig().getFontDetailsSupplier().measureString(
                            box.getNode(),
                            -1,
                            text);
                    ((PageNumberPlaceholderBox) item).set(text, metrics.getWidth(), metrics);
                } catch (Exception e) {
                    throw new TypeSettingException(e);
                }
            }
        }
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
                if (quality == WORST_QUALITY - 1) {
                    if (Debug.isDebug()) {
                        LOGGER.log(Level.INFO, String.format(
                                "Paragraph at %s could not be split into lines properly using the Knuth-Plass algorithm. Instead using the first-fit algorithm now.",
                                textParagraph.getNode() != null && textParagraph.getNode().getTextNode() != null ? textParagraph.getNode().getTextNode().getTextPosition() : "UNKNOWN"
                        ));
                    }

                    // Do the first-fit algorithm instead
                    int currentLineNumber = 1;
                    double lineWidth = textParagraph.getLineWidth(currentLineNumber);
                    double currentWidth = 0;
                    List<BreakPoint> breakPoints = new ArrayList<>();
                    for (int i = 0; i < textParagraph.items().size(); i++) {
                        Item item = textParagraph.items().get(i);

                        boolean breakLine;
                        if (item.getType() == ItemType.PENALTY) {
                            // Check if is explicit line-break
                            breakLine = ((Penalty) item).isMandatoryLineBreak();
                        } else {
                            breakLine = currentWidth + item.getWidth() > lineWidth;
                        }

                        if (breakLine) {
                            breakPoints.add(new BreakPoint(i));

                            lineWidth = textParagraph.getLineWidth(++currentLineNumber);
                            currentWidth = 0;
                        }

                        currentWidth += item.getWidth();
                    }

                    return new KnuthPlassAlgorithm.LineBreakingResult(
                            breakPoints, new LineBreakingContext(textParagraph,
                            Integer.MAX_VALUE));
                }
            }
        }

        throw new TypeSettingException();
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
        return (lineWidth - lineMetrics.getMinWidth()) / lineMetrics.getWhiteSpaces();
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
