package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.impl.toc.TableOfContentsItemParagraph;
import de.be.thaw.typeset.page.impl.PageNumberPlaceholderElement;
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
        StyleValue lineHeightStyleValue = paragraph.getNode().getStyles().resolve(StyleType.LINE_HEIGHT).orElseThrow();
        double lineHeight;
        if (lineHeightStyleValue.unit().isEmpty()) {
            // Is relative line-height -> Calculate line height from the font size
            StyleValue fontSizeValue = paragraph.getNode().getStyles().resolve(StyleType.FONT_SIZE).orElseThrow();
            lineHeight = Unit.convert(fontSizeValue.doubleValue(), fontSizeValue.unit().orElse(Unit.POINTS), Unit.POINTS) * lineHeightStyleValue.doubleValue();
        } else {
            lineHeight = Unit.convert(lineHeightStyleValue.doubleValue(), lineHeightStyleValue.unit().orElseThrow(), Unit.POINTS);
        }

        double pageNumberMaxX = ctx.getConfig().getPageSize().getWidth() - ctx.getConfig().getPageInsets().getRight();

        FontDetailsSupplier.StringMetrics metrics;
        try {
            metrics = ctx.getConfig().getFontDetailsSupplier().measureString(paragraph.getNode(), -1, "99999");
        } catch (Exception e) {
            throw new TypeSettingException(e);
        }

        ctx.pushPageElement(new PageNumberPlaceholderElement(
                metrics,
                paragraph.getNode(),
                ctx.getCurrentPageNumber(),
                metrics.getBaseline(),
                new Size(p.getPageNumberWidth(), lineHeight),
                new Position(pageNumberMaxX, ctx.getPositionContext().getY() - lineHeight)
        ));
    }

}
