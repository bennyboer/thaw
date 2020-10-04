package de.be.thaw.typeset.knuthplass.config.util.image;

import de.be.thaw.util.Size;
import de.be.thaw.util.unit.Unit;

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

    /**
     * Get the unit used to specify the image size.
     *
     * @return unit
     */
    Unit getSizeUnit();

}
