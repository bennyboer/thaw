package de.be.thaw.font.util.file;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 * Representation of a font collection file.
 * For example *.ttc files.
 */
public class FontCollectionFile implements FontFile {

    /**
     * Location of the file.
     */
    private final String location;

    /**
     * Font files in the collection.
     */
    private final Font[] fonts;

    public FontCollectionFile(String location) throws IOException, FontFormatException {
        this.location = location;

        fonts = Font.createFonts(new File(location));
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Get the fonts in the collection.
     *
     * @return fonts
     */
    public Font[] getFonts() {
        return fonts;
    }

}
