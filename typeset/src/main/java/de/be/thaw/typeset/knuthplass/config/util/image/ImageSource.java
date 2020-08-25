package de.be.thaw.typeset.knuthplass.config.util.image;

import de.be.thaw.util.Size;

/**
 * An image source.
 */
public interface ImageSource {

    /**
     * Get the image size.
     *
     * @return size of the original image
     */
    Size getSize();

}
