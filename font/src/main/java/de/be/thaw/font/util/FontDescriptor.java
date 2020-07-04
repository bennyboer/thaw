package de.be.thaw.font.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Descriptor of a font file.
 */
public class FontDescriptor {

    /**
     * Path to the font file location.
     */
    private final String location;

    /**
     * The parsed font to get some information from.
     */
    private final Font font;

    public FontDescriptor(String location) throws IOException, FontFormatException {
        this.location = location;

        font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(location));
    }

    /**
     * Get the parsed font.
     *
     * @return font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Get the fonts file location.
     *
     * @return file location
     */
    public String getLocation() {
        return location;
    }

}
