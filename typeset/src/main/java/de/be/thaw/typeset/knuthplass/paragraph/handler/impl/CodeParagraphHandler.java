package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import com.rtfparserkit.rtf.Command;
import com.rtfparserkit.rtf.CommandType;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.value.BooleanStyleValue;
import de.be.thaw.style.model.style.value.ColorStyleValue;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.style.model.style.value.FontVariantStyleValue;
import de.be.thaw.style.model.style.value.StringStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.code.CodeParagraph;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.page.impl.RectangleElement;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.unit.BaseUnit;
import de.be.thaw.util.unit.Unit;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * Paragraph handler dealing with typesetting code block paragraphs.
 */
public class CodeParagraphHandler implements ParagraphTypesetHandler {

    /**
     * The default code paragraph caption prefix.
     */
    private static final String DEFAULT_CODE_CAPTION_PREFIX = "Listing";

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.CODE;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        CodeParagraph codeParagraph = (CodeParagraph) paragraph;

        Styles styles = paragraph.getNode().getStyles();

        // Calculate margins
        final double marginTop = styles.resolve(StyleType.MARGIN_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginBottom = styles.resolve(StyleType.MARGIN_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        ctx.getPositionContext().increaseY(marginTop);

        // Parse the RTF code
        IRtfSource source = new RtfStreamSource(new ByteArrayInputStream(codeParagraph.getRtfCode().getBytes(StandardCharsets.UTF_8)));
        IRtfParser parser = new StandardRtfParser();
        try {
            parser.parse(source, new RTFCodeParser(codeParagraph, ctx));
        } catch (IOException e) {
            throw new TypeSettingException("Could not typeset code block paragraph properly", e);
        } catch (ParseCancelException e) {
            throw new TypeSettingException(String.format(
                    "An error occurred while trying to parse the syntax highlighted code (RTF) for a code block. Exception was: '%s'",
                    e.getCause().getMessage()
            ), e.getCause());
        }

        // Add caption (if any)
        if (codeParagraph.getCaption().isPresent()) {
            String caption = codeParagraph.getCaption().orElseThrow();
            addCaption(caption, codeParagraph, ctx);
        }

        ctx.getPositionContext().increaseY(marginBottom);
    }

    /**
     * Add a caption under the code paragraph.
     *
     * @param caption   to add
     * @param paragraph the code paragraph
     * @param ctx       the typesetting context
     * @throws TypeSettingException in case the caption could not be added properly
     */
    private void addCaption(String caption, CodeParagraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
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

        // Typeset the caption
        StyleModel styleModel = new DefaultStyleModel();
        styleModel.addBlock(new StyleBlock(
                new StyleSelectorBuilder().setTargetName("document").build(),
                Map.ofEntries(
                        Map.entry(StyleType.FIRST_LINE_INDENT, new DoubleStyleValue(0.0, Unit.MILLIMETER))
                )
        ));

        List<Page> pages = ctx.typesetThawTextFormat(String.format(
                "**%s %d**: %s",
                paragraph.getCaptionPrefix() != null ? paragraph.getCaptionPrefix() : ctx.getConfig().getProperties().getOrDefault("code.caption.prefix", DEFAULT_CODE_CAPTION_PREFIX),
                ctx.getDocument().getReferenceModel().getReferenceNumber(paragraph.getNode().getId()),
                caption
        ), paragraph.getDefaultLineWidth() - marginLeft - marginRight, styleModel);

        // Re-layout the elements below the code paragraph
        double endY = ctx.getPositionContext().getY() + paddingBottom;

        double startY = endY;
        double maxY = endY;

        double x = ctx.getPositionContext().getX();
        for (Page page : pages) {
            for (Element element : page.getElements()) {
                double oldX = element.getPosition().getX();
                double oldY = element.getPosition().getY();

                // Set new position
                AbstractElement abstractElement = (AbstractElement) element;
                abstractElement.setPosition(new Position(
                        oldX + x,
                        oldY + endY
                ));

                // Check if new line
                if (element.getPosition().getY() + element.getSize().getHeight() > maxY) {
                    maxY = element.getPosition().getY() + element.getSize().getHeight();

                    // Check if there is enough space for the next line
                    double captionHeight = maxY - startY;
                    double availableHeight = ctx.getAvailableHeight() - captionHeight;
                    if (availableHeight < element.getSize().getHeight()) {
                        ctx.pushPage(); // Create next page

                        startY = ctx.getConfig().getPageInsets().getTop();
                        endY = -oldY + startY;
                        maxY = startY + element.getSize().getHeight();

                        // Update current element position again
                        abstractElement.setPosition(new Position(
                                oldX + x,
                                oldY + endY
                        ));
                    }
                }

                ctx.pushPageElement(element);
            }
        }

        ctx.getPositionContext().setY(maxY);
    }

    /**
     * Context of a group in an RTF document.
     */
    private static class RTFGroupContext {

        /**
         * Parent context (if any).
         */
        @Nullable
        private final RTFGroupContext parent;

        /**
         * Whether bold is enabled
         */
        @Nullable
        private Boolean bold;

        /**
         * Whether italic is enabled.
         */
        @Nullable
        private Boolean italic;

        /**
         * The currently used font color.
         */
        @Nullable
        private Color color;

        /**
         * Whether the group allows for adding page elements.
         */
        private Boolean acceptElements;

        public RTFGroupContext(@Nullable RTFGroupContext parent) {
            this.parent = parent;
        }

        public Optional<RTFGroupContext> getParent() {
            return Optional.ofNullable(parent);
        }

        public boolean isBold() {
            if (bold != null) {
                return bold;
            } else if (parent != null) {
                return parent.isBold();
            } else {
                return false;
            }
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public boolean isItalic() {
            if (italic != null) {
                return italic;
            } else if (parent != null) {
                return parent.isItalic();
            } else {
                return false;
            }
        }

        public void setItalic(boolean italic) {
            this.italic = italic;
        }

        public Color getColor() {
            if (color != null) {
                return color;
            } else if (parent != null) {
                return parent.getColor();
            } else {
                return new Color(0.0, 0.0, 0.0, 1.0);
            }
        }

        public void setColor(@Nullable Color color) {
            this.color = color;
        }

        public boolean isAcceptElements() {
            if (acceptElements != null) {
                return acceptElements;
            } else if (parent != null) {
                return parent.isAcceptElements();
            } else {
                return true;
            }
        }

        public void setAcceptElements(boolean acceptElements) {
            this.acceptElements = acceptElements;
        }

    }

    /**
     * Exception thrown when parsing is cancelled.
     */
    private static class ParseCancelException extends RuntimeException {

        public ParseCancelException(Throwable cause) {
            super(cause);
        }

    }

    /**
     * Parser parsing RTF code.
     */
    private static class RTFCodeParser implements IRtfListener {

        /**
         * Colors listed in the RTF color table.
         */
        private final List<Color> colors = new ArrayList<>();

        /**
         * Stack holding always the current RTF group context (colors, bold, italic, ...).
         */
        private final Stack<RTFGroupContext> ctxStack = new Stack<>();

        /**
         * Whether the color table is currently being parsed.
         */
        private boolean isParseColors = false;

        private int tmpRed = -1;
        private int tmpGreen = -1;
        private int tmpBlue = -1;

        /**
         * The code paragraph parsing RTF code from.
         */
        private final CodeParagraph codeParagraph;

        /**
         * The typesetting context.
         */
        private final TypeSettingContext ctx;

        /**
         * Line height to use.
         */
        private final double lineHeight;

        /**
         * Left margin
         */
        private final double marginLeft;

        /**
         * Right margin.
         */
        private final double marginRight;

        /**
         * Left padding.
         */
        private final double paddingLeft;

        /**
         * Right padding.
         */
        private final double paddingRight;

        /**
         * Top padding.
         */
        private final double paddingTop;

        /**
         * Bottom padding.
         */
        private final double paddingBottom;

        /**
         * Whether to show line numbers.
         */
        private final boolean showLineNumbers;

        /**
         * Font family to use for drawing line numbers.
         */
        private final String lineNumberFontFamily;

        /**
         * Font size to draw line numbers with.
         */
        private final double lineNumberFontSize;

        /**
         * Color to draw line numbers with.
         */
        private final Color lineNumberColor;

        /**
         * The monospaced font family to use.
         */
        private final String monoSpacedFontFamily;

        /**
         * Whether to draw the line number the next token.
         */
        private boolean drawLineNumberNextToken = true;

        /**
         * The current line number to draw.
         */
        private int currentLineNumberToDraw = 0;

        /**
         * Counter of line numbers.
         */
        private int lineNumberCounter = 1;

        /**
         * The startY of the block on the current page.
         */
        private double startY;

        /**
         * Element index on the current page for the first page element of this block.
         */
        private int startElementIndex;

        /**
         * Background color.
         */
        private final Color backgroundColor;

        /**
         * The border widths.
         */
        private final Insets borderWidths;

        /**
         * The border radius.
         */
        private final Insets borderRadius;

        /**
         * Colors of the border sides.
         */
        private final Color[] borderColors;

        /**
         * Border styles.
         */
        private final LineStyle[] borderStyles;

        RTFCodeParser(CodeParagraph codeParagraph, TypeSettingContext ctx) {
            this.codeParagraph = codeParagraph;
            this.ctx = ctx;

            startElementIndex = ctx.getCurrentPageElements().size();

            // Fetch some styles from the code paragraph node.
            Styles styles = codeParagraph.getNode().getStyles();

            marginLeft = styles.resolve(StyleType.MARGIN_LEFT)
                    .orElseThrow()
                    .doubleValue(Unit.POINTS);
            marginRight = styles.resolve(StyleType.MARGIN_RIGHT)
                    .orElseThrow()
                    .doubleValue(Unit.POINTS);

            paddingLeft = styles.resolve(StyleType.PADDING_LEFT)
                    .orElseThrow()
                    .doubleValue(Unit.POINTS);
            paddingRight = styles.resolve(StyleType.PADDING_RIGHT)
                    .orElseThrow()
                    .doubleValue(Unit.POINTS);
            paddingTop = styles.resolve(StyleType.PADDING_TOP)
                    .orElseThrow()
                    .doubleValue(Unit.POINTS);
            paddingBottom = styles.resolve(StyleType.PADDING_BOTTOM)
                    .orElseThrow()
                    .doubleValue(Unit.POINTS);

            StyleValue lineHeightStyleValue = styles.resolve(StyleType.LINE_HEIGHT).orElseThrow();
            if (lineHeightStyleValue.unit().getBaseUnit() == BaseUnit.UNITARY) {
                // Is relative line-height -> Calculate line height from the font size
                lineHeight = styles.resolve(StyleType.FONT_SIZE)
                        .orElseThrow()
                        .doubleValue(Unit.POINTS) * lineHeightStyleValue.doubleValue(Unit.UNITARY);
            } else {
                lineHeight = lineHeightStyleValue.doubleValue(Unit.POINTS);
            }

            showLineNumbers = styles.resolve(StyleType.SHOW_LINE_NUMBERS).orElse(new BooleanStyleValue(true)).booleanValue();
            lineNumberFontFamily = styles.resolve(StyleType.LINE_NUMBER_FONT_FAMILY).orElseThrow().value();

            lineNumberFontSize = styles.resolve(StyleType.LINE_NUMBER_FONT_SIZE)
                    .orElse(new DoubleStyleValue(10.0, Unit.POINTS))
                    .doubleValue(Unit.POINTS);

            lineNumberColor = styles.resolve(StyleType.LINE_NUMBER_COLOR).orElse(new ColorStyleValue(new Color(0.4, 0.4, 0.4))).colorValue();
            monoSpacedFontFamily = styles.resolve(StyleType.FONT_FAMILY).orElseThrow().value();

            backgroundColor = styles.resolve(StyleType.BACKGROUND_COLOR).map(StyleValue::colorValue).orElse(new Color(1.0, 1.0, 1.0, 1.0));
            borderWidths = new Insets(
                    styles.resolve(StyleType.BORDER_TOP_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                    styles.resolve(StyleType.BORDER_RIGHT_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                    styles.resolve(StyleType.BORDER_BOTTOM_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                    styles.resolve(StyleType.BORDER_LEFT_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0)
            );
            borderRadius = new Insets(
                    styles.resolve(StyleType.BORDER_RADIUS_TOP).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                    styles.resolve(StyleType.BORDER_RADIUS_RIGHT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                    styles.resolve(StyleType.BORDER_RADIUS_BOTTOM).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                    styles.resolve(StyleType.BORDER_RADIUS_LEFT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0)
            );
            borderColors = new Color[]{
                    new Color(0.0, 0.0, 0.0),
                    new Color(0.0, 0.0, 0.0),
                    new Color(0.0, 0.0, 0.0),
                    new Color(0.0, 0.0, 0.0)
            };
            styles.resolve(StyleType.BORDER_TOP_COLOR).ifPresent(v -> borderColors[0] = v.colorValue());
            styles.resolve(StyleType.BORDER_RIGHT_COLOR).ifPresent(v -> borderColors[1] = v.colorValue());
            styles.resolve(StyleType.BORDER_BOTTOM_COLOR).ifPresent(v -> borderColors[2] = v.colorValue());
            styles.resolve(StyleType.BORDER_LEFT_COLOR).ifPresent(v -> borderColors[3] = v.colorValue());
            borderStyles = new LineStyle[]{
                    LineStyle.SOLID,
                    LineStyle.SOLID,
                    LineStyle.SOLID,
                    LineStyle.SOLID
            };
            styles.resolve(StyleType.BORDER_TOP_STYLE).ifPresent(v -> borderStyles[0] = LineStyle.valueOf(v.fillStyle().name()));
            styles.resolve(StyleType.BORDER_RIGHT_STYLE).ifPresent(v -> borderStyles[1] = LineStyle.valueOf(v.fillStyle().name()));
            styles.resolve(StyleType.BORDER_BOTTOM_STYLE).ifPresent(v -> borderStyles[2] = LineStyle.valueOf(v.fillStyle().name()));
            styles.resolve(StyleType.BORDER_LEFT_STYLE).ifPresent(v -> borderStyles[3] = LineStyle.valueOf(v.fillStyle().name()));

            // Add the default color (index 0).
            colors.add(new Color(0.0, 0.0, 0.0));

            // Check if there is enough space for the next line
            double availableHeight = ctx.getAvailableHeight();
            if (availableHeight < lineHeight) {
                // Not enough space for this line left on the current page -> Create next page
                try {
                    ctx.pushPage();
                } catch (TypeSettingException e) {
                    cancelParsing(e);
                }
            }

            startY = ctx.getPositionContext().getY();

            // Initialize position context
            ctx.getPositionContext().increaseY(paddingTop);
            ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + marginLeft + paddingLeft);
        }

        @Override
        public void processDocumentStart() {
            // Do nothing.
        }

        @Override
        public void processDocumentEnd() {
            pushRectangleElementIfNecessary();

            ctx.getPositionContext().increaseY(paddingBottom);
        }

        @Override
        public void processGroupStart() {
            ctxStack.push(new RTFGroupContext(ctxStack.isEmpty() ? null : ctxStack.peek()));
        }

        @Override
        public void processGroupEnd() {
            if (isParseColors) {
                isParseColors = false;
            }

            ctxStack.pop();
        }

        @Override
        public void processCharacterBytes(byte[] data) {
            // Do nothing.
        }

        @Override
        public void processBinaryBytes(byte[] data) {
            // Do nothing.
        }

        /**
         * Process a string in color parsing state.
         *
         * @param string that occurred
         */
        private void onProcessStringParseColors(String string) {
            if (string.equals(";") && tmpBlue > -1) {
                colors.add(new Color(tmpRed / 255.0, tmpGreen / 255.0, tmpBlue / 255.0));
            }
        }

        /**
         * Process a string in default state.
         *
         * @param string that occurred
         */
        private void onProcessStringDefault(String string) {
            boolean isBlank = string.isBlank();

            // Extract leading and trailing white spaces from string
            int leadingSpacesCount = isBlank ? string.length() : string.length() - string.stripLeading().length();
            int trailingSpacesCount = isBlank ? 0 : string.length() - string.stripTrailing().length();
            string = string.trim();

            // Add leading white spaces as text element
            if (leadingSpacesCount > 0) {
                addTextElement(" ".repeat(leadingSpacesCount));
            }

            if (!string.isBlank()) {
                // Split parts by white spaces
                String[] parts = string.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];

                    addTextElement(i == parts.length - 1 ? part : part + " ");
                }
            }

            // Add trailing white spaces as text element
            if (trailingSpacesCount > 0) {
                addTextElement(" ".repeat(trailingSpacesCount));
            }
        }

        /**
         * Add a text element for the passed string that has been parsed.
         *
         * @param str that has been parsed
         */
        private void addTextElement(String str) {
            // Create styles from current context
            RTFGroupContext curCtx = ctxStack.peek();

            FontVariant variant = FontVariant.MONOSPACE;
            if (curCtx.isBold() && curCtx.isItalic()) {
                variant = FontVariant.BOLD_ITALIC;
            } else if (curCtx.isBold()) {
                variant = FontVariant.BOLD;
            } else if (curCtx.isItalic()) {
                variant = FontVariant.ITALIC;
            }

            Styles dummyDocumentNodeStyles = new Styles(codeParagraph.getNode().getStyles());
            dummyDocumentNodeStyles.overrideStyle(StyleType.FONT_FAMILY, new StringStyleValue(monoSpacedFontFamily));
            dummyDocumentNodeStyles.overrideStyle(StyleType.FONT_VARIANT, new FontVariantStyleValue(variant));
            dummyDocumentNodeStyles.overrideStyle(StyleType.COLOR, new ColorStyleValue(curCtx.getColor()));

            DocumentNode dummyDocumentNode = new DocumentNode(new TextNode(str, null), codeParagraph.getNode(), dummyDocumentNodeStyles);

            // Measure string
            FontDetailsSupplier.StringMetrics metrics;
            try {
                metrics = ctx.getConfig().getFontDetailsSupplier().measureString(dummyDocumentNode, -1, str);
            } catch (Exception e) {
                cancelParsing(new TypeSettingException(e));
                return;
            }

            // Check whether the current line should be shown (defined by start and end line in the code paragraph).
            boolean showLine = lineNumberCounter >= codeParagraph.getStartLine() && lineNumberCounter <= codeParagraph.getEndLine();
            if (!showLine) {
                return;
            }

            // Draw line numbers (if necessary)
            if (showLineNumbers && drawLineNumberNextToken) {
                drawLineNumberNextToken = false;

                try {
                    drawLineNumber(++currentLineNumberToDraw, codeParagraph, ctx);
                } catch (TypeSettingException e) {
                    cancelParsing(e);
                }
            }

            // Check if text element has enough space in the current line -> otherwise break line
            double maxX = ctx.getConfig().getPageInsets().getLeft() + codeParagraph.getDefaultLineWidth() - marginRight - paddingRight;
            if (ctx.getPositionContext().getX() + metrics.getWidth() > maxX) {
                breakLine(false);
            }

            ctx.pushPageElement(new TextElement(
                    str,
                    metrics,
                    dummyDocumentNode,
                    ctx.getCurrentPageNumber(),
                    metrics.getFontSize(),
                    new Size(metrics.getWidth(), metrics.getHeight()),
                    new Position(ctx.getPositionContext().getX(), ctx.getPositionContext().getY())
            ));

            ctx.getPositionContext().increaseX(metrics.getWidth());
        }

        @Override
        public void processString(String string) {
            if (isParseColors) {
                onProcessStringParseColors(string);
            } else if (ctxStack.peek().isAcceptElements()) {
                onProcessStringDefault(string);
            }
        }

        /**
         * Process the passed comment while in color parse state.
         *
         * @param command      to process
         * @param parameter    the parameter (if any)
         * @param hasParameter whether the parameter is to be honored
         * @param optional     whether optional
         */
        private void processCommandWhileInParseColors(Command command, int parameter, boolean hasParameter, boolean optional) {
            if (command.getCommandType() == CommandType.Value) {
                switch (command.getCommandName()) {
                    case "red" -> tmpRed = parameter;
                    case "green" -> tmpGreen = parameter;
                    case "blue" -> tmpBlue = parameter;
                }
            }
        }

        /**
         * Process a command of type SYMBOL.
         *
         * @param command      to process
         * @param parameter    the parameter (if any)
         * @param hasParameter whether the parameter is to be honored
         * @param optional     whether optional
         */
        private void processSymbolCommand(Command command, int parameter, boolean hasParameter, boolean optional) {
            if (command.getCommandName().equals("par") || command.getCommandName().equals("line")) {
                breakLine(true);
            }
        }

        /**
         * Break the current line.
         *
         * @param explicit whether we are dealing with an explicit line break or because the line ran out of space
         */
        private void breakLine(boolean explicit) {
            boolean displayLine = lineNumberCounter >= codeParagraph.getStartLine() && lineNumberCounter <= codeParagraph.getEndLine();
            if (displayLine) {
                // Draw line numbers if it has not been drawn for this line yet
                if (showLineNumbers && drawLineNumberNextToken) {
                    try {
                        drawLineNumber(++currentLineNumberToDraw, codeParagraph, ctx);
                    } catch (TypeSettingException e) {
                        cancelParsing(e);
                    }
                }

                // Adjust position context for the next line
                ctx.getPositionContext().increaseY(lineHeight);
                ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + marginLeft + paddingLeft);

                // Check if there is enough space for the next line
                double availableHeight = ctx.getAvailableHeight();
                if (availableHeight < lineHeight) {
                    // Not enough space for this line left on the current page -> Create next page

                    // First create rectangle element for the background (if necessary)
                    pushRectangleElementIfNecessary();

                    try {
                        ctx.pushPage();
                    } catch (TypeSettingException e) {
                        cancelParsing(e);
                    }

                    startY = ctx.getPositionContext().getY();
                    startElementIndex = ctx.getCurrentPageElements().size();
                }
            } else {
                currentLineNumberToDraw++;
            }

            if (explicit) {
                lineNumberCounter++; // Increase line number counter

                drawLineNumberNextToken = true;
            }
        }

        /**
         * Push a rectangle element for the background if necessary.
         */
        private void pushRectangleElementIfNecessary() {
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
         * Process a command of type VALUE.
         *
         * @param command      to process
         * @param parameter    the parameter (if any)
         * @param hasParameter whether the parameter is to be honored
         * @param optional     whether optional
         */
        private void processValueCommand(Command command, int parameter, boolean hasParameter, boolean optional) {
            if (command.getCommandName().equals("cf")) {
                // Set foreground color
                ctxStack.peek().setColor(colors.get(parameter));
            }
        }

        /**
         * Process a command of type TOGGLE.
         *
         * @param command      to process
         * @param parameter    the parameter (if any)
         * @param hasParameter whether the parameter is to be honored
         * @param optional     whether optional
         */
        private void processToggleCommand(Command command, int parameter, boolean hasParameter, boolean optional) {
            if (command.getCommandName().equals("b")) {
                ctxStack.peek().setBold(!hasParameter);
            } else if (command.getCommandName().equals("i")) {
                ctxStack.peek().setItalic(!hasParameter);
            }
        }

        /**
         * Process the passed comment while in default state.
         *
         * @param command      to process
         * @param parameter    the parameter (if any)
         * @param hasParameter whether the parameter is to be honored
         * @param optional     whether optional
         */
        private void processCommandDefault(Command command, int parameter, boolean hasParameter, boolean optional) {
            switch (command.getCommandType()) {
                case Symbol -> processSymbolCommand(command, parameter, hasParameter, optional);
                case Value -> processValueCommand(command, parameter, hasParameter, optional);
                case Toggle -> processToggleCommand(command, parameter, hasParameter, optional);
            }
        }

        @Override
        public void processCommand(Command command, int parameter, boolean hasParameter, boolean optional) {
            if (command.getCommandType() == CommandType.Destination) {
                if (!command.getCommandName().equals("rtf")) {
                    ctxStack.peek().setAcceptElements(false);
                }

                if (command.getCommandName().equals("colortbl")) {
                    isParseColors = true;
                }
            }

            if (isParseColors) {
                processCommandWhileInParseColors(command, parameter, hasParameter, optional);
            } else {
                processCommandDefault(command, parameter, hasParameter, optional);
            }
        }

        /**
         * Cancel the parsing process.
         *
         * @param exception that caused the cancel
         * @throws RuntimeException thrown indicating that parsing failed
         */
        private void cancelParsing(TypeSettingException exception) {
            throw new ParseCancelException(exception);
        }

        /**
         * Draw the passed line number in front of a code line.
         *
         * @param lineNumber to draw
         * @param paragraph  the paragraph
         * @param ctx        the typesetting context
         */
        private void drawLineNumber(int lineNumber, CodeParagraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
            // Stringify line number
            String lineNumberStr = String.valueOf(lineNumber);

            // Create dummy document node representing the line number string
            Styles lineNumberNodeStyles = new Styles(paragraph.getNode().getStyles());
            lineNumberNodeStyles.overrideStyle(StyleType.FONT_FAMILY, new StringStyleValue(lineNumberFontFamily));
            lineNumberNodeStyles.overrideStyle(StyleType.FONT_VARIANT, new FontVariantStyleValue(FontVariant.MONOSPACE));
            lineNumberNodeStyles.overrideStyle(StyleType.FONT_SIZE, new DoubleStyleValue(lineNumberFontSize, Unit.POINTS));
            lineNumberNodeStyles.overrideStyle(StyleType.COLOR, new ColorStyleValue(lineNumberColor));

            DocumentNode lineNumberDocumentNode = new DocumentNode(new TextNode(lineNumberStr, null), paragraph.getNode(), lineNumberNodeStyles);

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
                    lineNumberStrMetrics.getFontSize(),
                    new Size(lineNumberStrMetrics.getWidth(), lineNumberStrMetrics.getHeight()),
                    new Position(ctx.getConfig().getPageInsets().getLeft() - lineNumberStrMetrics.getWidth() - 10.0 + marginLeft + paddingLeft, ctx.getPositionContext().getY())
            ));
        }

    }

}
