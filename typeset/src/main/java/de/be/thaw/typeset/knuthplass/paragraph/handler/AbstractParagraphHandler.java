package de.be.thaw.typeset.knuthplass.paragraph.handler;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Position;
import de.be.thaw.util.unit.Unit;

import java.util.List;
import java.util.Map;

/**
 * An abstract paragraph typeset handler.
 */
public abstract class AbstractParagraphHandler implements ParagraphTypesetHandler {

    /**
     * Add the passed caption string under the paragraph.
     *
     * @param caption       to add
     * @param captionPrefix prefix of the caption
     * @param paragraph     the current paragraph
     * @param isFloating    whether the paragraph is floating
     * @param startY        start y-offset of the caption
     * @param startX        start x-offset of the caption
     * @param width         the caption is allowed to take
     * @param margin        margin settings of the paragraph
     * @param padding       padding settings of the paragraph
     * @param ctx           the typesetting context
     * @throws TypeSettingException in case the caption could not be added properly
     */
    public void addCaption(
            String caption,
            String captionPrefix,
            Paragraph paragraph,
            boolean isFloating,
            double startY,
            double startX,
            double width,
            Insets margin,
            Insets padding,
            TypeSettingContext ctx
    ) throws TypeSettingException {
        // Typeset the caption
        StyleModel styleModel = new DefaultStyleModel();
        styleModel.addBlock(new StyleBlock(
                new StyleSelectorBuilder().setTargetName("document").build(),
                Map.ofEntries(
                        Map.entry(StyleType.FIRST_LINE_INDENT, new DoubleStyleValue(0.0, Unit.MILLIMETER))
                )
        ));

        List<Page> pages = ctx.typesetThawTextFormat(String.format(
                "**%s %d**: %s",
                captionPrefix,
                ctx.getDocument().getReferenceModel().getReferenceNumber(paragraph.getNode().getId()),
                caption
        ), width, styleModel);

        // Re-layout the elements below the image paragraph
        double maxY = startY;
        double endY = startY;
        for (Page page : pages) {
            for (Element element : page.getElements()) {
                double oldX = element.getPosition().getX();
                double oldY = element.getPosition().getY();

                // Set new position
                AbstractElement abstractElement = (AbstractElement) element;
                abstractElement.setPosition(new Position(
                        oldX + startX,
                        oldY + endY
                ));

                // Check if new line
                if (element.getPosition().getY() + element.getSize().getHeight() > maxY) {
                    maxY = element.getPosition().getY() + element.getSize().getHeight();

                    // Check if there is enough space for the next line -> only when not floating
                    if (!isFloating) {
                        double captionHeight = maxY - startY;
                        double availableHeight = ctx.getAvailableHeight() - captionHeight;
                        if (availableHeight < element.getSize().getHeight()) {
                            ctx.pushPage(); // Create next page

                            startY = ctx.getConfig().getPageInsets().getTop();
                            endY = -oldY + startY;
                            maxY = startY + element.getSize().getHeight();

                            // Update current element position again
                            abstractElement.setPosition(new Position(
                                    oldX + startX,
                                    oldY + endY
                            ));
                        }
                    }
                }

                ctx.pushPageElement(element);
            }
        }

        maxY += margin.getBottom() + padding.getBottom();

        // Adjust floating configuration (if needed)
        if (isFloating) {
            ctx.getFloatConfig().setFloatUntilY(maxY);
        } else {
            ctx.getPositionContext().setY(maxY);
        }
    }

}
