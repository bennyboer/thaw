package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.ExportContext;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.util.PdfImageSource;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.ImageElement;

import java.io.IOException;
import java.util.Set;

/**
 * Exporter for images.
 */
public class ImageElementExporter implements ElementExporter {

    /**
     * Supported element types by this exporter.
     */
    private static final Set<ElementType> SUPPORTED_TYPES = Set.of(ElementType.IMAGE);

    @Override
    public Set<ElementType> supportedElementTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public void export(Element element, ExportContext ctx) throws ExportException {
        ImageElement ie = (ImageElement) element;

        PdfImageSource src = (PdfImageSource) ie.getSrc();

        double y = ctx.getCurrentPage().getMediaBox().getUpperRightY() - ie.getPosition().getY() - element.getSize().getHeight();

        try {
            ctx.getContentStream().drawImage(
                    src.getImage(),
                    (float) element.getPosition().getX(),
                    (float) y,
                    (float) element.getSize().getWidth(),
                    (float) element.getSize().getHeight()
            );
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

}
