package de.be.thaw.typeset.knuthplass.paragraph.handler.impl;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.math.MathParagraph;
import de.be.thaw.typeset.page.impl.MathExpressionElement;
import de.be.thaw.typeset.page.impl.RectangleElement;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.unit.Unit;

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

        // Fetch some styles
        Styles styles = paragraph.getNode().getStyles();

        final double marginTop = styles.resolve(StyleType.MARGIN_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginBottom = styles.resolve(StyleType.MARGIN_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingTop = styles.resolve(StyleType.PADDING_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingBottom = styles.resolve(StyleType.PADDING_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        ctx.getPositionContext().increaseY(marginTop + paddingTop);

        double width = mathParagraph.getExpression().getSize().getWidth();

        // Check if there is enough space for the paragraph
        if (ctx.getAvailableHeight() < mathParagraph.getExpression().getSize().getHeight()) {
            // Not enough space for this expression left on the current page -> Create next page
            ctx.pushPage();
        }

        double y = ctx.getPositionContext().getY();
        double maxWidth = ctx.getConfig().getPageSize().getWidth() - (ctx.getConfig().getPageInsets().getLeft() + ctx.getConfig().getPageInsets().getRight());
        double x = ctx.getConfig().getPageInsets().getLeft();
        if (mathParagraph.getAlignment() == HorizontalAlignment.CENTER) {
            x += (maxWidth - width) / 2;
        } else if (mathParagraph.getAlignment() == HorizontalAlignment.RIGHT) {
            x += maxWidth - width;
        }

        MathExpressionElement element = new MathExpressionElement(
                mathParagraph.getExpression(),
                ctx.getCurrentPageNumber(),
                mathParagraph.getExpression().getSize(),
                new Position(x, y),
                mathParagraph.getNode(),
                false,
                0.0
        );

        pushRectangleElementIfNecessary(ctx, mathParagraph, element);

        ctx.pushPageElement(element);
        ctx.getPositionContext().increaseY(mathParagraph.getExpression().getSize().getHeight() + marginBottom + paddingBottom);
    }

    /**
     * Push a rectangle element for the background if necessary.
     */
    private void pushRectangleElementIfNecessary(TypeSettingContext ctx, MathParagraph paragraph, MathExpressionElement element) {
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
        final double paddingTop = styles.resolve(StyleType.PADDING_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        Color backgroundColor = styles.resolve(StyleType.BACKGROUND_COLOR).map(StyleValue::colorValue).orElse(new Color(1.0, 1.0, 1.0, 1.0));
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
                element.getSize().getHeight() + paddingBottom + paddingTop
        );
        Position position = new Position(
                ctx.getConfig().getPageInsets().getLeft() + marginLeft,
                ctx.getPositionContext().getY() - paddingTop
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

            ctx.pushPageElement(rect);
        }
    }

}
