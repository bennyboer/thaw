package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import com.rtfparserkit.rtf.Command;
import com.rtfparserkit.rtf.CommandType;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.TextStyle;
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
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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

        InsetsStyle insetsStyle = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.INSETS,
                style -> Optional.ofNullable((InsetsStyle) style)
        ).orElseThrow();

        ctx.getPositionContext().increaseY(insetsStyle.getTop());

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
            addCaption(caption, codeParagraph, insetsStyle, ctx);
        }

        ctx.getPositionContext().increaseY(insetsStyle.getBottom());
    }

    /**
     * Add a caption under the code paragraph.
     *
     * @param caption     to add
     * @param paragraph   the code paragraph
     * @param insetsStyle of the code paragraph to use
     * @param ctx         the typesetting context
     * @throws TypeSettingException in case the caption could not be added properly
     */
    private void addCaption(String caption, CodeParagraph paragraph, InsetsStyle insetsStyle, TypeSettingContext ctx) throws TypeSettingException {
        // Typeset the caption
        Map<StyleType, Style> styles = new HashMap<>();
        styles.put(StyleType.TEXT, new TextStyle(0.0, null, null, null, null, null, null, null));
        StyleBlock documentStyleBlock = new StyleBlock("DOCUMENT", styles);
        DefaultStyleModel styleModel = new DefaultStyleModel(new HashMap<>());
        styleModel.addBlock(documentStyleBlock.getName(), documentStyleBlock);

        List<Page> pages = ctx.typesetThawTextFormat(String.format(
                "**%s %d**: %s",
                paragraph.getCaptionPrefix() != null ? paragraph.getCaptionPrefix() : ctx.getConfig().getProperties().getOrDefault("code.caption.prefix", DEFAULT_CODE_CAPTION_PREFIX),
                ctx.getDocument().getReferenceModel().getReferenceNumber(paragraph.getNode().getId()),
                caption
        ), paragraph.getDefaultLineWidth() - insetsStyle.getLeft() - insetsStyle.getRight(), styleModel);

        // Re-layout the elements below the code paragraph
        double endY = ctx.getPositionContext().getY();
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
        private ColorStyle color;

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

        public ColorStyle getColor() {
            if (color != null) {
                return color;
            } else if (parent != null) {
                return parent.getColor();
            } else {
                return new ColorStyle(0.0, 0.0, 0.0, 1.0);
            }
        }

        public void setColor(@Nullable ColorStyle color) {
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
        private final List<ColorStyle> colors = new ArrayList<>();

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
         * Insets to honor.
         */
        private final InsetsStyle insetsStyle;

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
        private final ColorStyle lineNumberColor;

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

        RTFCodeParser(CodeParagraph codeParagraph, TypeSettingContext ctx) {
            this.codeParagraph = codeParagraph;
            this.ctx = ctx;

            // Fetch some styles from the code paragraph node.
            lineHeight = codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.TEXT,
                    style -> Optional.ofNullable(((TextStyle) style).getLineHeight())
            ).orElse(codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.FONT,
                    style -> Optional.ofNullable(((FontStyle) style).getSize())
            ).orElseThrow());
            insetsStyle = codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.INSETS,
                    style -> Optional.ofNullable((InsetsStyle) style)
            ).orElseThrow();
            showLineNumbers = codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.TEXT,
                    style -> Optional.ofNullable(((TextStyle) style).isShowLineNumbers())
            ).orElse(true);
            lineNumberFontFamily = codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.TEXT,
                    style -> Optional.ofNullable(((TextStyle) style).getLineNumberFontFamily())
            ).orElse("Consolas");
            lineNumberFontSize = codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.TEXT,
                    style -> Optional.ofNullable(((TextStyle) style).getLineNumberFontSize())
            ).orElse(12.0);
            lineNumberColor = codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.TEXT,
                    style -> Optional.ofNullable(((TextStyle) style).getLineNumberColor())
            ).orElse(null);
            monoSpacedFontFamily = codeParagraph.getNode().getStyle().getStyleAttribute(
                    StyleType.FONT,
                    style -> Optional.ofNullable(((FontStyle) style).getMonoSpacedFontFamily())
            ).orElseThrow();

            // Add the default color (index 0).
            colors.add(new ColorStyle(0.0, 0.0, 0.0, 1.0));

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

            // Initialize position context
            ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + insetsStyle.getLeft());
        }

        @Override
        public void processDocumentStart() {
            // Do nothing.
        }

        @Override
        public void processDocumentEnd() {
            // Do nothing.
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
                colors.add(new ColorStyle(tmpRed / 255.0, tmpGreen / 255.0, tmpBlue / 255.0, 1.0));
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

            Map<StyleType, Style> styles = new HashMap<>();
            styles.put(StyleType.FONT, new FontStyle(monoSpacedFontFamily, variant, null, curCtx.getColor(), null, KerningMode.NATIVE));
            DocumentNode dummyDocumentNode = new DocumentNode(new TextNode(str, null), codeParagraph.getNode(), new DocumentNodeStyle(codeParagraph.getNode().getStyle(), styles));

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
            double maxX = ctx.getConfig().getPageInsets().getLeft() + codeParagraph.getDefaultLineWidth() - insetsStyle.getRight();
            if (ctx.getPositionContext().getX() + metrics.getWidth() > maxX) {
                breakLine(false);
            }

            ctx.pushPageElement(new TextElement(
                    str,
                    metrics,
                    dummyDocumentNode,
                    ctx.getCurrentPageNumber(),
                    metrics.getBaseline(),
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
                ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + insetsStyle.getLeft());

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
            } else {
                currentLineNumberToDraw++;
            }

            if (explicit) {
                lineNumberCounter++; // Increase line number counter

                drawLineNumberNextToken = true;
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
                    lineNumberStrMetrics.getBaseline(),
                    new Size(lineNumberStrMetrics.getWidth(), lineNumberStrMetrics.getHeight()),
                    new Position(ctx.getConfig().getPageInsets().getLeft() - lineNumberStrMetrics.getWidth() - 10.0 + insetsStyle.getLeft(), ctx.getPositionContext().getY())
            ));
        }

    }

}
