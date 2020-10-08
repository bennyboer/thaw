package de.be.thaw.typeset.knuthplass.paragraph.handler.impl.image;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.AbstractParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.image.ImageParagraph;
import de.be.thaw.typeset.page.impl.ImageElement;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.unit.Unit;

/**
 * Handler dealing with typesetting image paragraphs.
 */
public class ImageParagraphHandler extends AbstractParagraphHandler {

    /**
     * The default caption prefix for figures.
     */
    private static final String DEFAULT_FIGURE_CAPTION_PREFIX = "Figure";

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.IMAGE;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        ImageParagraph imageParagraph = (ImageParagraph) paragraph;

        Styles styles = paragraph.getNode().getStyles();

        // Calculate margins
        final double marginTop = styles.resolve(StyleType.MARGIN_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginBottom = styles.resolve(StyleType.MARGIN_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginLeft = styles.resolve(StyleType.MARGIN_LEFT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double marginRight = styles.resolve(StyleType.MARGIN_RIGHT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        // Calculate paddings (Only affecting the image, not the caption)
        final double paddingTop = styles.resolve(StyleType.PADDING_TOP)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingBottom = styles.resolve(StyleType.PADDING_BOTTOM)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingLeft = styles.resolve(StyleType.PADDING_LEFT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);
        final double paddingRight = styles.resolve(StyleType.PADDING_RIGHT)
                .orElseThrow()
                .doubleValue(Unit.POINTS);

        ctx.getPositionContext().increaseY(marginTop + paddingTop);

        double width = imageParagraph.getDefaultLineWidth()
                - (marginLeft + marginRight)
                - (paddingLeft + paddingRight);

        final double imgWidth = Unit.convert(imageParagraph.getSrc().getSize().getWidth(), imageParagraph.getSrc().getSizeUnit(), Unit.POINTS);
        final double imgHeight = Unit.convert(imageParagraph.getSrc().getSize().getHeight(), imageParagraph.getSrc().getSizeUnit(), Unit.POINTS);

        double ratio = imgWidth / imgHeight;
        double height = width / ratio;

        double maxWidth = ctx.getConfig().getPageSize().getWidth()
                - (ctx.getConfig().getPageInsets().getLeft() + ctx.getConfig().getPageInsets().getRight())
                - (marginLeft + marginRight)
                - (paddingLeft + paddingRight);
        double x = ctx.getConfig().getPageInsets().getLeft();
        if (imageParagraph.getAlignment() == HorizontalAlignment.CENTER) {
            x += (maxWidth - width) / 2;
        } else if (imageParagraph.getAlignment() == HorizontalAlignment.RIGHT) {
            x += maxWidth - width;
        }

        x += marginLeft + paddingLeft;

        boolean isFloating = imageParagraph.isFloating() && imageParagraph.getAlignment() != HorizontalAlignment.CENTER;

        // Check if there is enough space for the image -> only when not floating!
        if (!isFloating) {
            double availableHeight = ctx.getAvailableHeight();
            if (availableHeight < height) {
                ctx.pushPage(); // Not enough space for this image on the current page -> create next page
            }
        }

        ImageElement imageElement = new ImageElement(
                imageParagraph.getSrc(),
                imageParagraph.getNode(),
                ctx.getCurrentPageNumber(),
                new Size(width, height),
                new Position(x, ctx.getPositionContext().getY())
        );
        ctx.pushPageElement(imageElement);

        double endY = ctx.getPositionContext().getY() + height + marginBottom + paddingBottom;
        if (isFloating) {
            ctx.getFloatConfig().setFloatUntilY(endY);
            ctx.getFloatConfig().setFloatWidth(width + marginLeft + marginRight + paddingLeft + paddingRight);
            ctx.getFloatConfig().setFloatIndent(imageParagraph.getAlignment() == HorizontalAlignment.LEFT ? ctx.getFloatConfig().getFloatWidth() : 0);
        } else {
            ctx.getPositionContext().setY(endY);
        }

        // Deal with the image paragraphs caption (if any).
        if (imageParagraph.getCaption().isPresent()) {
            addCaption(
                    imageParagraph.getCaption().orElseThrow(),
                    imageParagraph.getCaptionPrefix() != null ?
                            imageParagraph.getCaptionPrefix() :
                            (String) ctx.getConfig().getProperties().getOrDefault("image.caption.prefix", DEFAULT_FIGURE_CAPTION_PREFIX),
                    imageParagraph,
                    paragraph.isFloating() && imageParagraph.getAlignment() != HorizontalAlignment.CENTER,
                    imageElement.getPosition().getY() + imageElement.getSize().getHeight() + paddingBottom,
                    imageElement.getPosition().getX(),
                    imageElement.getSize().getWidth(),
                    new Insets(marginTop, marginRight, marginBottom, marginLeft),
                    new Insets(paddingTop, paddingRight, paddingBottom, paddingLeft),
                    ctx
            );
        }
    }

}
