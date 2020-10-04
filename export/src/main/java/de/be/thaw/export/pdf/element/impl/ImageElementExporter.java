package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.export.pdf.util.PdfImageSource;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.ImageElement;
import de.be.thaw.util.unit.Unit;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

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
            PDPageContentStream out = ctx.getContentStream();

            if (src.getImage() instanceof PDImageXObject) {
                PDImageXObject imageXObject = (PDImageXObject) src.getImage();

                out.drawImage(
                        imageXObject,
                        (float) element.getPosition().getX(),
                        (float) y,
                        (float) element.getSize().getWidth(),
                        (float) element.getSize().getHeight()
                );
            } else if (src.getImage() instanceof PDFormXObject) {
                PDFormXObject formXObject = (PDFormXObject) src.getImage();

                out.saveGraphicsState();

                double actualWidth = Unit.convert(src.getSize().getWidth(), src.getSizeUnit(), Unit.POINTS);
                double preferredWidth = element.getSize().getWidth();

                double ratio = preferredWidth / actualWidth;

                Matrix scaleMatrix = Matrix.getScaleInstance(
                        (float) ratio,
                        (float) ratio
                );
                Matrix transformMatrix = Matrix.getTranslateInstance(
                        (float) element.getPosition().getX(),
                        (float) y
                );
                transformMatrix.concatenate(scaleMatrix);

                out.transform(transformMatrix);

                out.drawForm(formXObject);

                out.restoreGraphicsState();
            } else {
                throw new ExportException(String.format(
                        "Image object of type %s are not yet supported",
                        src.getImage().getClass().getSimpleName()
                ));
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public void afterExport(Element element, ExportContext ctx) throws ExportException {
        // Nothing to do after export
    }

}
