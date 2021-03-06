package de.be.thaw.export.pdf.font;

import de.be.thaw.export.pdf.font.exception.FontParseException;
import de.be.thaw.font.AbstractFont;
import de.be.thaw.font.opentype.gpos.GlyphPositioningTable;
import de.be.thaw.font.util.CharacterSize;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.typeset.kerning.glyph.Coordinate;
import de.be.thaw.typeset.kerning.glyph.Glyph;
import de.be.thaw.typeset.kerning.optical.OpticalKerningTable;
import de.be.thaw.util.Size;
import de.be.thaw.util.cache.CacheUtil;
import de.be.thaw.util.os.OperatingSystem;
import de.be.thaw.util.os.exception.CouldNotDetermineOperatingSystemException;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphDescription;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.OpenTypeScript;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TTFTable;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ThawPdfFont extends AbstractFont {

    /**
     * The latin alphabet.
     */
    private static final String LATIN_ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Characters for which we do optical kerning.
     */
    private static final char[] OPTICAL_KERNING_CHARACTERS = (LATIN_ALPHABET + LATIN_ALPHABET.toUpperCase()).toCharArray();

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
     * Kerning table calculated from optical kerning.
     */
    private OpticalKerningTable opticalKerningTable;

    /**
     * A map of characters.
     */
    private CmapSubtable characterMap;

    /**
     * Units per EM of this font.
     */
    private double unitsPerEm;

    /**
     * Kerning mode to use.
     */
    private KerningMode kerningMode = KerningMode.NATIVE;

    /**
     * The glyph positioning table.
     */
    @Nullable
    private GlyphPositioningTable gposTable;

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
     * Create new PDF font.
     *
     * @param ttf      the true type font to use
     * @param document to embed font in
     * @throws IOException in case the font could not be loaded properly
     */
    public ThawPdfFont(TrueTypeFont ttf, PDDocument document) throws IOException {
        pdFont = PDType0Font.load(document, ttf, true);

        initForTTF(ttf);
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

        // Fetch GPOS table for getting kerning information
        TTFTable gpos = ttf.getTableMap().get(GlyphPositioningTable.TAG);
        if (gpos != null) {
            // Parse GPOS table to fetch pair positioning (kerning) information
            gposTable = new GlyphPositioningTable(new ByteArrayInputStream(ttf.getTableBytes(gpos)));
        } else {
            // Fallback to the older KERN table
            KerningTable kerningTable = ttf.getKerning();
            if (kerningTable != null) {
                kerningSubtable = kerningTable.getHorizontalKerningSubtable();
            }
        }

        // Load or calculate optical kerning table
        File rootCacheDir = CacheUtil.getCacheRootDir();
        File opticalKerningCacheDir = new File(rootCacheDir, "optical-kerning");
        if (!opticalKerningCacheDir.exists()) {
            opticalKerningCacheDir.mkdirs();
        }

        File[] cacheFiles = opticalKerningCacheDir.listFiles();
        for (File cacheFile : cacheFiles) {
            if (cacheFile.getName().equals(ttf.getName())) {
                // Found cached optical kerning table for the font -> load it
                try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(cacheFile)))) {
                    opticalKerningTable = (OpticalKerningTable) ois.readObject();
                } catch (ClassNotFoundException e) {
                    throw new IOException(e);
                } catch (InvalidClassException e) {
                    // Recalculate optical kerning table
                }
            }
        }

        if (opticalKerningTable == null) {
            // Calculate the optical kerning table
            opticalKerningTable = calculateOpticalKerningTable();

            // Write kerning table to cache file
            File outFile = new File(opticalKerningCacheDir, ttf.getName());
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)))) {
                oos.writeObject(opticalKerningTable);
            }
        }
    }

    /**
     * Get the character map for the passed TrueType font.
     *
     * @param ttf to get character map for
     * @return character map
     * @throws IOException in case the character map could not be loaded
     */
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
    public double getAscent(double fontSize) {
        return pdFont.getFontDescriptor().getAscent() * fontSize / unitsPerEm;
    }

    @Override
    public double getDescent(double fontSize) {
        return pdFont.getFontDescriptor().getDescent() * fontSize / unitsPerEm;
    }

    @Override
    public CharacterSize getCharacterSize(int character, double fontSize) throws Exception {
        int glyphID = characterMap.getGlyphId(character);

        double width = pdFont.getWidth(glyphID) * fontSize / 1000;
        double height = 0;

        GlyphData data = glyphTable.getGlyph(glyphID);
        double descent = 0;
        double ascent = 0;
        if (data != null) {
            height = (data.getYMaximum() - data.getYMinimum()) * fontSize / unitsPerEm;
            ascent = data.getYMaximum() * fontSize / unitsPerEm;
            descent = -data.getYMinimum() * fontSize / unitsPerEm;
        }

        return new CharacterSize(width, height, ascent, descent);
    }

    @Override
    public double getKerningAdjustment(int leftChar, int rightChar, double fontSize) {
        switch (kerningMode) {
            case NATIVE -> {
                if (gposTable != null) {
                    return gposTable.getKerning(characterMap.getGlyphId(leftChar), characterMap.getGlyphId(rightChar), OpenTypeScript.getScriptTags(leftChar), null) * fontSize / unitsPerEm;
                } else if (kerningSubtable != null) {
                    return kerningSubtable.getKerning(characterMap.getGlyphId(leftChar), characterMap.getGlyphId(rightChar)) * fontSize / unitsPerEm;
                }
            }
            case OPTICAL -> {
                if (opticalKerningTable != null) {
                    return opticalKerningTable.getKerning(characterMap.getGlyphId(leftChar), characterMap.getGlyphId(rightChar)) * fontSize / unitsPerEm;
                }
            }
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

    /**
     * Calculate the optical kerning table.
     *
     * @return optical kerning table
     * @throws IOException in case the glyphs could not be fetched from the font
     */
    private OpticalKerningTable calculateOpticalKerningTable() throws IOException {
        List<Glyph> glyphs = new ArrayList<>(OPTICAL_KERNING_CHARACTERS.length);
        for (char c : OPTICAL_KERNING_CHARACTERS) {
            int glyphID = characterMap.getGlyphId(c);

            GlyphData data = glyphTable.getGlyph(glyphID);
            if (data != null) {
                glyphs.add(convertToGlyph(data, glyphID, c));
            }
        }

        OpticalKerningTable okt = new OpticalKerningTable();
        okt.init(glyphs.toArray(Glyph[]::new));

        return okt;
    }

    /**
     * Convert the passed arguments to a glyph.
     *
     * @param data      of the glyph
     * @param glyphID   the glyph ID
     * @param codePoint the actual code point the glyph is representing
     * @return glyph
     */
    private Glyph convertToGlyph(GlyphData data, int glyphID, int codePoint) {
        Size size = new Size(data.getXMaximum() - data.getXMinimum(), data.getYMaximum() - data.getYMinimum());
        Coordinate position = new Coordinate(data.getXMinimum(), data.getYMinimum());

        List<List<Coordinate>> contours = new ArrayList<>(Math.max(0, data.getNumberOfContours()));
        GlyphDescription description = data.getDescription();
        int start = 0;
        for (int c = 0; c < data.getNumberOfContours(); c++) {
            int end = description.getEndPtOfContours(c);

            List<Coordinate> contour = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                short x = description.getXCoordinate(i);
                short y = description.getYCoordinate(i);

                contour.add(new Coordinate(x, y));
            }

            contours.add(contour);

            start = end + 1;
        }

        return new Glyph(glyphID, codePoint, size, position, contours);
    }

    public KerningMode getKerningMode() {
        return kerningMode;
    }

    public void setKerningMode(KerningMode kerningMode) {
        this.kerningMode = kerningMode;
    }

}
