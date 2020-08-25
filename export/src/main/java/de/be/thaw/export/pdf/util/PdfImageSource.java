package de.be.thaw.export.pdf.util;

import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.util.Size;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Image source for the PDF exporter.
 */
public class PdfImageSource implements ImageSource {

    /**
     * The image.
     */
    private final PDImageXObject image;

    /**
     * Size of the image.
     */
    private final Size size;

    public PdfImageSource(PDImageXObject image, double pointsPerPx) {
        this.image = image;

        size = new Size(image.getWidth() * pointsPerPx, image.getHeight() * pointsPerPx);
    }

    @Override
    public Size getSize() {
        return size;
    }

    public PDImageXObject getImage() {
        return image;
    }

}
