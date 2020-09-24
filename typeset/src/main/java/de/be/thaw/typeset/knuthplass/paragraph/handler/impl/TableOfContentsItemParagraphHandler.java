package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.util.FillStyle;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.impl.toc.TableOfContentsItemParagraph;
import de.be.thaw.typeset.knuthplass.util.LazySize;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.impl.LineElement;
import de.be.thaw.typeset.page.impl.PageNumberPlaceholderElement;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.unit.Unit;

import java.util.List;

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
        int oldPageElementsSize = ctx.getCurrentPageElements().size();

        // Typeset headline as usual
        super.handle(paragraph, ctx);

        TableOfContentsItemParagraph p = (TableOfContentsItemParagraph) paragraph;

        // Fetch some styles
        Styles styles = paragraph.getNode().getStyles();

        FillStyle fillStyle = styles.resolve(StyleType.FILL).orElseThrow().fillStyle();
        final double fillSize = styles.resolve(StyleType.FILL_SIZE)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        double pageNumberMaxX = ctx.getConfig().getPageSize().getWidth() - ctx.getConfig().getPageInsets().getRight();

        FontDetailsSupplier.StringMetrics maxPageNumberMetrics;
        FontDetailsSupplier.StringMetrics numberingMetrics;
        double spaceWidth;
        try {
            maxPageNumberMetrics = ctx.getConfig().getFontDetailsSupplier().measureString(paragraph.getNode(), -1, "99999");
            numberingMetrics = ctx.getConfig().getFontDetailsSupplier().measureString(paragraph.getNode(), -1, p.getNumberingString());
            spaceWidth = ctx.getConfig().getFontDetailsSupplier().getSpaceWidth(paragraph.getNode());
        } catch (Exception e) {
            throw new TypeSettingException(e);
        }

        boolean isNumberingOnLastPage = ctx.getCurrentPageElements().size() <= oldPageElementsSize;
        List<Element> numberingPageElements;
        if (isNumberingOnLastPage) {
            numberingPageElements = ctx.getPages().get(ctx.getPages().size() - 1).getElements();
        } else {
            numberingPageElements = ctx.getCurrentPageElements();
        }

        TextElement firstHeadlineTextElement = (TextElement) numberingPageElements.get(oldPageElementsSize);
        TextElement lastTextElement = (TextElement) ctx.getCurrentPageElements().get(ctx.getCurrentPageElements().size() - 1);

        // Add the numbering string text element before the headline text that has already been typeset
        numberingPageElements.add(new TextElement(
                p.getNumberingString(),
                numberingMetrics,
                p.getNode(),
                ctx.getCurrentPageNumber(),
                lastTextElement.getBaseline(),
                new Size(numberingMetrics.getWidth(), lastTextElement.getSize().getHeight()),
                new Position(firstHeadlineTextElement.getPosition().getX() - numberingMetrics.getWidth() - spaceWidth, firstHeadlineTextElement.getPosition().getY())
        ));

        // Add the placeholder element for the page number
        PageNumberPlaceholderElement placeholderElement = new PageNumberPlaceholderElement(
                maxPageNumberMetrics,
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
