package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.util.FillStyle;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.impl.toc.TableOfContentsItemParagraph;
import de.be.thaw.typeset.knuthplass.util.LazySize;
import de.be.thaw.typeset.page.impl.LineElement;
import de.be.thaw.typeset.page.impl.PageNumberPlaceholderElement;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.unit.Unit;

/**
 * Handler dealing with typesetting a table of contents item paragraph.
 */
public class TableOfContentsItemParagraphHandler extends TextParagraphHandler {

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.TOC_ITEM;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        super.handle(paragraph, ctx);

        TableOfContentsItemParagraph p = (TableOfContentsItemParagraph) paragraph;

        // Fetch some styles
        Styles styles = paragraph.getNode().getStyles();

        StyleValue lineHeightStyleValue = styles.resolve(StyleType.LINE_HEIGHT).orElseThrow();
        double lineHeight;
        if (lineHeightStyleValue.unit().isEmpty()) {
            // Is relative line-height -> Calculate line height from the font size
            StyleValue fontSizeValue = paragraph.getNode().getStyles().resolve(StyleType.FONT_SIZE).orElseThrow();
            lineHeight = Unit.convert(fontSizeValue.doubleValue(), fontSizeValue.unit().orElse(Unit.POINTS), Unit.POINTS) * lineHeightStyleValue.doubleValue();
        } else {
            lineHeight = Unit.convert(lineHeightStyleValue.doubleValue(), lineHeightStyleValue.unit().orElseThrow(), Unit.POINTS);
        }

        FillStyle fillStyle = styles.resolve(StyleType.FILL).orElseThrow().fillStyle();
        StyleValue fillSizeValue = styles.resolve(StyleType.FILL_SIZE).orElseThrow();
        final double fillSize = Unit.convert(fillSizeValue.doubleValue(), fillSizeValue.unit().orElse(Unit.MILLIMETER), Unit.POINTS);

        double pageNumberMaxX = ctx.getConfig().getPageSize().getWidth() - ctx.getConfig().getPageInsets().getRight();

        FontDetailsSupplier.StringMetrics metrics;
        try {
            metrics = ctx.getConfig().getFontDetailsSupplier().measureString(paragraph.getNode(), -1, "99999");
        } catch (Exception e) {
            throw new TypeSettingException(e);
        }

        TextElement lastTextElement = (TextElement) ctx.getCurrentPageElements().get(ctx.getCurrentPageElements().size() - 1);

        PageNumberPlaceholderElement placeholderElement = new PageNumberPlaceholderElement(
                metrics,
                paragraph.getNode(),
                ctx.getCurrentPageNumber(),
                lastTextElement.getBaseline(),
                new Size(p.getPageNumberWidth(), lastTextElement.getSize().getHeight()),
                new Position(pageNumberMaxX, lastTextElement.getPosition().getY())
        );
        ctx.pushPageElement(placeholderElement);

        if (fillStyle != FillStyle.NONE) {
            LineStyle style = LineStyle.SOLID;
            if (fillStyle == FillStyle.DOTTED) {
                style = LineStyle.DOTTED;
            }

            double startX = lastTextElement.getPosition().getX() + lastTextElement.getSize().getWidth() + 5.0;
            double endX = pageNumberMaxX - p.getPageNumberWidth();
            double width = endX - startX;

            if (width > 0) {
                double y = lastTextElement.getPosition().getY() + lastTextElement.getBaseline();

                ctx.pushPageElement(new LineElement(
                        ctx.getCurrentPageNumber(),
                        new LazySize(() -> placeholderElement.getPosition().getX() - startX - 5.0, () -> 0.0),
                        new Position(
                                startX,
                                y
                        ),
                        style,
                        fillSize,
                        styles.resolve(StyleType.COLOR).orElseThrow().colorValue()
                ));
            }
        }
    }

}
