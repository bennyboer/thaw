package de.be.thaw.export.pdf.util;

import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.util.Size;
import de.be.thaw.util.unit.Unit;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;

/**
 * Image source for the PDF exporter.
 */
public class PdfImageSource implements ImageSource {

    /**
     * The image object (PDImageXObject, PDFormXObject (PDF)).
     */
    private final PDXObject image;

    /**
     * Size of the image.
     */
    private final Size size;

    /**
     * Unit used for the size.
     */
    private final Unit sizeUnit;

    public PdfImageSource(PDXObject image, Size size, Unit sizeUnit) {
        this.image = image;
        this.size = size;
        this.sizeUnit = sizeUnit;
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public Unit getSizeUnit() {
        return sizeUnit;
    }

    /**
     * Get the image object to use.
     *
     * @return image object
     */
    public PDXObject getImage() {
        return image;
    }

}
