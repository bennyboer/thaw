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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Paragraph handler dealing with typesetting code block paragraphs.
 */
public class CodeParagraphHandler implements ParagraphTypesetHandler {

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.CODE;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        CodeParagraph codeParagraph = (CodeParagraph) paragraph;

        // Fetch some styles from the paragraph node
        final double lineHeight = codeParagraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineHeight())
        ).orElse(codeParagraph.getNode().getStyle().getStyleAttribute(
                StyleType.FONT,
                style -> Optional.ofNullable(((FontStyle) style).getSize())
        ).orElseThrow());
        final InsetsStyle insetsStyle = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.INSETS,
                style -> Optional.ofNullable((InsetsStyle) style)
        ).orElseThrow();
        final boolean showLineNumbers = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).isShowLineNumbers())
        ).orElse(true);

        // Prepare collections used during RTF parsing
        List<ColorStyle> colors = new ArrayList<>();
        colors.add(new ColorStyle(0.0, 0.0, 0.0, 1.0)); // Add the default color (index 0)
        Stack<RTFGroupContext> ctxStack = new Stack<>();

        // Check if there is enough space for the next line
        double availableHeight = ctx.getAvailableHeight();
        if (availableHeight < lineHeight) {
            // Not enough space for this line left on the current page -> Create next page
            ctx.pushPage();
        }

        AtomicInteger currentLineNumber = new AtomicInteger(0); // Counter for the current line number
        if (showLineNumbers) {
            drawLineNumber(currentLineNumber.incrementAndGet(), codeParagraph, ctx);
        }

        // Parse the RTF code
        IRtfSource source = new RtfStreamSource(new ByteArrayInputStream(codeParagraph.getRtfCode().getBytes(StandardCharsets.UTF_8)));
        IRtfParser parser = new StandardRtfParser();
        try {
            parser.parse(source, new IRtfListener() {

                /**
                 * Whether the color table is currently being parsed.
                 */
                private boolean isParseColors = false;

                private int tmpRed = -1;
                private int tmpGreen = -1;
                private int tmpBlue = -1;

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

                @Override
                public void processString(String string) {
                    if (isParseColors) {
                        if (string.equals(";") && tmpBlue > -1) {
                            colors.add(new ColorStyle(tmpRed / 255.0, tmpGreen / 255.0, tmpBlue / 255.0, 1.0));
                        }
                    } else if (ctxStack.peek().isAcceptElements()) {
                        // Add text element!

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

                        String monoSpacedFontFamily = codeParagraph.getNode().getStyle().getStyleAttribute(
                                StyleType.FONT,
                                style -> Optional.ofNullable(((FontStyle) style).getMonoSpacedFontFamily())
                        ).orElseThrow();

                        Map<StyleType, Style> styles = new HashMap<>();
                        styles.put(StyleType.FONT, new FontStyle(monoSpacedFontFamily, variant, null, curCtx.getColor(), null, KerningMode.NATIVE));
                        DocumentNode dummyDocumentNode = new DocumentNode(new TextNode(string, null), codeParagraph.getNode(), new DocumentNodeStyle(codeParagraph.getNode().getStyle(), styles));

                        FontDetailsSupplier.StringMetrics metrics;
                        try {
                            metrics = ctx.getConfig().getFontDetailsSupplier().measureString(dummyDocumentNode, -1, string);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        ctx.pushPageElement(new TextElement(
                                string,
                                metrics,
                                dummyDocumentNode,
                                ctx.getCurrentPageNumber(),
                                metrics.getBaseline(),
                                new Size(metrics.getWidth(), metrics.getHeight()),
                                new Position(ctx.getPositionContext().getX(), ctx.getPositionContext().getY())
                        ));

                        ctx.getPositionContext().increaseX(metrics.getWidth());
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
                        if (command.getCommandType() == CommandType.Value) {
                            switch (command.getCommandName()) {
                                case "red" -> tmpRed = parameter;
                                case "green" -> tmpGreen = parameter;
                                case "blue" -> tmpBlue = parameter;
                            }
                        }
                    } else {
                        switch (command.getCommandType()) {
                            case Symbol -> {
                                if (command.getCommandName().equals("par") || command.getCommandName().equals("line")) {
                                    // Explicit line break
                                    ctx.getPositionContext().increaseY(lineHeight);
                                    ctx.getPositionContext().setX(ctx.getConfig().getPageInsets().getLeft() + insetsStyle.getLeft());

                                    // Check if there is enough space for the next line
                                    double availableHeight = ctx.getAvailableHeight();
                                    if (availableHeight < lineHeight) {
                                        // Not enough space for this line left on the current page -> Create next page
                                        try {
                                            ctx.pushPage();
                                        } catch (TypeSettingException e) {
                                            throw new RuntimeException(e); // TODO Make this more beautiful
                                        }
                                    }

                                    if (showLineNumbers) {
                                        try {
                                            drawLineNumber(currentLineNumber.incrementAndGet(), codeParagraph, ctx);
                                        } catch (TypeSettingException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                            case Value -> {
                                if (command.getCommandName().equals("cf")) {
                                    // Set foreground color
                                    ctxStack.peek().setColor(colors.get(parameter));
                                }
                            }
                            case Toggle -> {
                                if (command.getCommandName().equals("b")) {
                                    ctxStack.peek().setBold(!hasParameter);
                                } else if (command.getCommandName().equals("i")) {
                                    ctxStack.peek().setItalic(!hasParameter);
                                }
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            throw new TypeSettingException("Could not typeset code block paragraph properly", e);
        }
    }

    /**
     * Draw the passed line number in front of a code line.
     *
     * @param lineNumber to draw
     * @param paragraph  the paragraph
     * @param ctx        the typesetting context
     */
    private void drawLineNumber(int lineNumber, CodeParagraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        final String lineNumberFontFamily = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineNumberFontFamily())
        ).orElse("Consolas");
        final Double lineNumberFontSize = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineNumberFontSize())
        ).orElse(12.0);
        final ColorStyle lineNumberColor = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineNumberColor())
        ).orElse(null);

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
                new Position(ctx.getConfig().getPageInsets().getLeft() - lineNumberStrMetrics.getWidth() - 10.0, ctx.getPositionContext().getY())
        ));
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

}
