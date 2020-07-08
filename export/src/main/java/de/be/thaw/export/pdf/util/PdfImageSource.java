package de.be.thaw.export.pdf.util;

import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.typeset.util.Size;
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

    public PdfImageSource(PDImageXObject image) {
        this.image = image;

        size = new Size(image.getWidth(), image.getHeight());
    }

    @Override
    public Size getSize() {
        return size;
    }

    public PDImageXObject getImage() {
        return image;
    }

}
