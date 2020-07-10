package de.be.thaw.export.pdf.font;

import de.be.thaw.export.pdf.font.exception.FontParseException;
import de.be.thaw.font.AbstractFont;
import de.be.thaw.font.util.OperatingSystem;
import de.be.thaw.font.util.Size;
import de.be.thaw.font.util.exception.CouldNotDetermineOperatingSystemException;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ThawPdfFont extends AbstractFont {

    /**
     * The ready to use PDF font.
     */
    private PDFont pdFont;

    /**
     * The glyph table of the font.
     */
    private GlyphTable glyphTable;

    /**
     * Table used to determine kerning adjustments.
     */
    private KerningSubtable kerningSubtable;

    /**
     * A map of characters.
     */
    private CmapSubtable characterMap;

    /**
     * Units per EM of this font.
     */
    private double unitsPerEm;

    /**
     * Create new PDF font.
     *
     * @param fontName name of the font
     * @param fontFile to create font from
     * @param document to embed font in
     */
    public ThawPdfFont(String fontName, File fontFile, PDDocument document) throws FontParseException {
        parseFont(fontName, fontFile, document);
    }

    /**
     * Parse the font.
     *
     * @param fontName name of the font
     * @param fontFile file of the font
     * @param document to embed font in
     * @throws FontParseException in case the font could not be parsed
     */
    private void parseFont(String fontName, File fontFile, PDDocument document) throws FontParseException {
        String fileName = fontFile.getName().toLowerCase();

        boolean isTrueType = fileName.endsWith(".ttf");
        boolean isTrueTypeCollection = fileName.endsWith(".ttc");

        if (isTrueType) {
            parseTrueTypeFont(fontName, fontFile, document);
        } else if (isTrueTypeCollection) {
            parseTrueTypeFontCollection(fontName, fontFile, document);
        } else {
            throw new FontParseException(String.format("Could not parse font from file '%s'", fileName));
        }
    }

    /**
     * Parse a true type font.
     *
     * @param fontName of the font
     * @param fontFile file of the font
     * @param document to embed font in
     * @throws FontParseException in case the font could not be parsed
     */
    private void parseTrueTypeFont(String fontName, File fontFile, PDDocument document) throws FontParseException {
        try {
            TrueTypeFont ttf = new TTFParser().parse(new FileInputStream(fontFile));

            pdFont = PDType0Font.load(document, ttf, true);

            initForTTF(ttf);
        } catch (IOException e) {
            throw new FontParseException(e);
        }
    }

    /**
     * Parse the font from a true type font collection file.
     *
     * @param fontName of the font
     * @param fontFile file containing the collection
     * @param document to embed the font in
     * @throws FontParseException in case the font could not be parsed
     */
    private void parseTrueTypeFontCollection(String fontName, File fontFile, PDDocument document) throws FontParseException {
        try {
            TrueTypeCollection collection = new TrueTypeCollection(fontFile);
            TrueTypeFont ttf = collection.getFontByName(fontName);

            pdFont = PDType0Font.load(document, ttf, true);

            initForTTF(ttf);
        } catch (IOException e) {
            throw new FontParseException(e);
        }
    }

    /**
     * Initialize for the passed true type font.
     *
     * @param ttf to initialize for
     */
    private void initForTTF(TrueTypeFont ttf) throws IOException {
        characterMap = getCharacterMap(ttf);
        glyphTable = ttf.getGlyph();
        unitsPerEm = ttf.getUnitsPerEm();

        KerningTable kerningTable = ttf.getKerning();
        if (kerningTable != null) {
            kerningSubtable = kerningTable.getHorizontalKerningSubtable();
        }
    }

    private CmapSubtable getCharacterMap(TrueTypeFont ttf) throws IOException {
        CmapSubtable cMap = ttf.getCmap().getSubtable(CmapTable.PLATFORM_UNICODE, CmapTable.ENCODING_UNICODE_2_0_FULL);
        if (cMap != null) {
            return cMap;
        }

        cMap = ttf.getCmap().getSubtable(CmapTable.PLATFORM_UNICODE, CmapTable.ENCODING_UNICODE_2_0_BMP);
        if (cMap != null) {
            return cMap;
        }

        try {
            OperatingSystem os = OperatingSystem.current();

            if (os == OperatingSystem.WINDOWS) {
                cMap = ttf.getCmap().getSubtable(CmapTable.PLATFORM_WINDOWS, CmapTable.ENCODING_WIN_UNICODE_FULL);
                if (cMap != null) {
                    return cMap;
                }

                cMap = ttf.getCmap().getSubtable(CmapTable.PLATFORM_WINDOWS, CmapTable.ENCODING_WIN_UNICODE_BMP);
            } else if (os == OperatingSystem.MAC_OS) {
                return ttf.getCmap().getSubtable(CmapTable.PLATFORM_MACINTOSH, CmapTable.ENCODING_MAC_ROMAN);
            }
        } catch (CouldNotDetermineOperatingSystemException e) {
            throw new IOException(e);
        }

        if (cMap == null) {
            throw new IOException("Could not load character map for the font");
        }

        return cMap;
    }

    @Override
    public Size getCharacterSize(int character, double fontSize) throws Exception {
        int glyphID = characterMap.getGlyphId(character);

        double width = pdFont.getWidth(glyphID) * fontSize / 1000;
        double height = 0;

        GlyphData data = glyphTable.getGlyph(glyphID);
        if (data != null) {
            height = (data.getYMaximum() - data.getYMinimum()) * fontSize / unitsPerEm;
        }

        return new Size(width, height);
    }

    @Override
    public double getKerningAdjustment(int leftChar, int rightChar, double fontSize) {
        if (kerningSubtable != null) {
            return kerningSubtable.getKerning(characterMap.getGlyphId(leftChar), characterMap.getGlyphId(rightChar)) * fontSize / unitsPerEm;
        }

        return 0;
    }

    /**
     * Get the PdFont.
     *
     * @return pdFont
     */
    public PDFont getPdFont() {
        return pdFont;
    }

}
