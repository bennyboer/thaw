package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.impl.toc.TableOfContentsItemParagraph;
import de.be.thaw.typeset.page.impl.PageNumberPlaceholderElement;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.Optional;

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
        double lineHeight = p.getNode().getStyle().getStyleAttribute(
                StyleType.TEXT,
                style -> Optional.ofNullable(((TextStyle) style).getLineHeight())
        ).orElse(p.getNode().getStyle().getStyleAttribute(
                StyleType.FONT,
                style -> Optional.ofNullable(((FontStyle) style).getSize())
        ).orElseThrow());

        double fontSize = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.FONT,
                style -> Optional.ofNullable(((FontStyle) style).getSize())
        ).orElse(11.0);

        double pageNumberMaxX = ctx.getConfig().getPageSize().getWidth() - ctx.getConfig().getPageInsets().getRight();
        ctx.pushPageElement(new PageNumberPlaceholderElement(
                new FontDetailsSupplier.StringMetrics(p.getPageNumberWidth(), lineHeight, new double[]{0}, fontSize, 0.0),
                paragraph.getNode(),
                ctx.getCurrentPageNumber(),
                new Size(p.getPageNumberWidth(), lineHeight),
                new Position(pageNumberMaxX, ctx.getPositionContext().getY() - lineHeight)
        ));
    }

}
