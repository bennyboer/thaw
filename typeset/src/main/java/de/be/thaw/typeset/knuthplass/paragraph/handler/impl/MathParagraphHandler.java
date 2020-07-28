package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.math.MathParagraph;
import de.be.thaw.typeset.page.impl.MathExpressionElement;
import de.be.thaw.util.Position;

/**
 * Handler dealing with typesetting math paragraphs.
 */
public class MathParagraphHandler implements ParagraphTypesetHandler {

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.MATH;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        MathParagraph mathParagraph = (MathParagraph) paragraph;

        double width = mathParagraph.getExpression().getSize().getWidth();

        double y = ctx.getPositionContext().getY();
        double maxWidth = ctx.getConfig().getPageSize().getWidth() - (ctx.getConfig().getPageInsets().getLeft() + ctx.getConfig().getPageInsets().getRight());
        double x = ctx.getConfig().getPageInsets().getLeft();
        if (mathParagraph.getAlignment() == HorizontalAlignment.CENTER) {
            x += (maxWidth - width) / 2;
        } else if (mathParagraph.getAlignment() == HorizontalAlignment.RIGHT) {
            x += maxWidth - width;
        }

        ctx.pushPageElement(new MathExpressionElement(
                mathParagraph.getExpression(),
                ctx.getCurrentPageNumber(),
                mathParagraph.getExpression().getSize(),
                new Position(x, y),
                mathParagraph.getNode()
        ));

        ctx.getPositionContext().increaseY(mathParagraph.getExpression().getSize().getHeight());
    }

}
