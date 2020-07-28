package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.image.ImageParagraph;
import de.be.thaw.typeset.page.impl.ImageElement;

import java.util.Optional;

/**
 * Handler dealing with typesetting image paragraphs.
 */
public class ImageParagraphHandler implements ParagraphTypesetHandler {

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.IMAGE;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        ImageParagraph imageParagraph = (ImageParagraph) paragraph;

        final InsetsStyle insetsStyle = paragraph.getNode().getStyle().getStyleAttribute(
                StyleType.INSETS,
                style -> Optional.ofNullable((InsetsStyle) style)
        ).orElseThrow();

        ctx.getPositionContext().increaseY(insetsStyle.getTop());

        double width = imageParagraph.getLineWidth(1);

        double ratio = imageParagraph.getSrc().getSize().getWidth() / imageParagraph.getSrc().getSize().getHeight();
        double height = width / ratio;

        double maxWidth = ctx.getConfig().getPageSize().getWidth() - (ctx.getConfig().getPageInsets().getLeft() + ctx.getConfig().getPageInsets().getRight()) - (insetsStyle.getLeft() + insetsStyle.getRight());
        double x = ctx.getConfig().getPageInsets().getLeft();
        if (imageParagraph.getAlignment() == HorizontalAlignment.CENTER) {
            x += (maxWidth - width) / 2;
        } else if (imageParagraph.getAlignment() == HorizontalAlignment.RIGHT) {
            x += maxWidth - width;
        }

        x += insetsStyle.getLeft();

        ctx.pushPageElement(new ImageElement(
                imageParagraph.getSrc(),
                imageParagraph.getNode(),
                ctx.getCurrentPageNumber(),
                new Size(width, height),
                new Position(x, ctx.getPositionContext().getY())
        ));

        if (imageParagraph.isFloating() && imageParagraph.getAlignment() != HorizontalAlignment.CENTER) {
            ctx.getFloatConfig().setFloatUntilY(ctx.getPositionContext().getY() + height + insetsStyle.getBottom());
            ctx.getFloatConfig().setFloatWidth(width + insetsStyle.getLeft() + insetsStyle.getRight());
            ctx.getFloatConfig().setFloatIndent(imageParagraph.getAlignment() == HorizontalAlignment.LEFT ? ctx.getFloatConfig().getFloatWidth() : 0);
        } else {
            ctx.getPositionContext().increaseY(height + insetsStyle.getBottom());
        }
    }

}
