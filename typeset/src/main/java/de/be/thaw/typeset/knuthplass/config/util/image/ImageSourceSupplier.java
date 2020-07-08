package de.be.thaw.typeset.knuthplass.config.util.image;

import java.io.IOException;

/**
 * Supplier for image sources.
 */
public interface ImageSourceSupplier {

    /**
     * Load the image source.
     *
     * @param src path of the image
     * @return the image source
     */
    ImageSource load(String src) throws IOException;

}
