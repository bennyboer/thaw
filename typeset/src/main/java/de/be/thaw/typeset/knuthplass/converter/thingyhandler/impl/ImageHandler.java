package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.image.ImageParagraph;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.unit.Unit;

import java.io.IOException;
import java.util.Set;

/**
 * Handler for image thingies.
 */
public class ImageHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("IMAGE");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        // Finalize the current paragraph
        ctx.finalizeParagraph();

        String caption = node.getOptions().get("caption");
        String captionPrefix = node.getOptions().get("caption-prefix");

        // Load the image
        ImageSource imgSrc;
        try {
            imgSrc = ctx.getConfig().getImageSourceSupplier().load(node.getOptions().get("src"));
        } catch (IOException e) {
            throw new DocumentConversionException(e);
        }

        String scaleStr = node.getOptions().get("scale");
        boolean scale = scaleStr == null || Boolean.parseBoolean(scaleStr);

        double imageWidth = scale ? ctx.getLineWidth() : Unit.convert(imgSrc.getSize().getWidth(), imgSrc.getSizeUnit(), Unit.POINTS);
        String imageWidthStr = node.getOptions().get("width");
        if (imageWidthStr != null) {
            try {
                imageWidth = Integer.parseInt(imageWidthStr);
            } catch (NumberFormatException e) {
                // Keep the default image width
            }
        }

        boolean floating = Boolean.parseBoolean(node.getOptions().get("float"));

        HorizontalAlignment alignment = HorizontalAlignment.CENTER;
        String alignmentStr = node.getOptions().get("alignment");
        if (alignmentStr != null) {
            alignment = HorizontalAlignment.valueOf(alignmentStr.toUpperCase());
        }

        ctx.setCurrentParagraph(new ImageParagraph(
                imageWidth,
                documentNode,
                imgSrc,
                floating,
                alignment,
                caption,
                captionPrefix
        ));
    }

}
