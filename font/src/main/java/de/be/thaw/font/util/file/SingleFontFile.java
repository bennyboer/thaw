package de.be.thaw.font.util.file;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 * File representing a single font.
 */
public class SingleFontFile implements FontFile {

    /**
     * Location of the file.
     */
    private final String location;

    /**
     * The parsed font.
     */
    private final Font font;

    public SingleFontFile(String location) throws IOException, FontFormatException {
        this.location = location;

        this.font = Font.createFont(Font.TRUETYPE_FONT, new File(location));
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getLocation() {
        return location;
    }

    /**
     * Get the parsed font.
     *
     * @return font
     */
    public Font getFont() {
        return font;
    }

}
