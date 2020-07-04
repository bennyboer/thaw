package de.be.thaw.font.util.file;

/**
 * Representation of a font file.
 */
public interface FontFile {

    /**
     * Whether the font file contains more than one font.
     *
     * @return whether collection
     */
    boolean isCollection();

    /**
     * Get the location of the file.
     *
     * @return file location
     */
    String getLocation();

}
