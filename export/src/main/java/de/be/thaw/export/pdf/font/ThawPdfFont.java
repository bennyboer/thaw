package de.be.thaw.export.pdf.font;

import de.be.thaw.export.pdf.font.exception.FontParseException;
import de.be.thaw.font.AbstractFont;
import de.be.thaw.font.util.OperatingSystem;
import de.be.thaw.font.util.Size;
import de.be.thaw.font.util.exception.CouldNotDetermineOperatingSystemException;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphDescription;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        // Load or calculate optical kerning table
        File opticalKerningCacheDir = Path.of(System.getProperty("user.home"), ".thaw", "optical-kerning").toFile();
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
                    e.printStackTrace();
                }
            }
        }

        if (opticalKerningTable == null) {
            // Calculate the optical kerning table
            System.out.println(String.format("Calculating optical kerning table for the font '%s'. This might take a while...", ttf.getName()));
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
        // TODO Remove - Only for testing
        try {
            Size charSize = getCharacterSize(rightChar, fontSize);

            double meanArea = opticalKerningTable.getMeanArea();

            int leftCharId = characterMap.getGlyphId(leftChar);
            int rightCharId = characterMap.getGlyphId(rightChar);

            if (leftCharId != 0 && rightCharId != 0) {
                double area = opticalKerningTable.getTable()[characterMap.getGlyphId(leftChar)][characterMap.getGlyphId(rightChar)];
                if (area == 0) {
                    return 0;
                }

                return (meanArea - area) / meanArea * charSize.getWidth() / 10;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private static class OpticalKerningTable implements Serializable {
        private final double meanArea;

        // TODO Optical kerning only for letters of the alphabet!
        private final double[][] table;

        public OpticalKerningTable(double meanArea, double[][] table) {
            this.meanArea = meanArea;
            this.table = table;
        }

        public double getMeanArea() {
            return meanArea;
        }

        public double[][] getTable() {
            return table;
        }
    }

    private OpticalKerningTable calculateOpticalKerningTable() throws IOException {
        double meanArea = 0;
        long counter = 0;

        GlyphData[] glyphs = glyphTable.getGlyphs();
        int len = glyphs.length;

        double[][] kerningTable = new double[len][len];

        for (int l = 0; l < len; l++) {
            GlyphData leftGlyph = glyphs[l];
            if (leftGlyph == null) {
                continue;
            }

            for (int r = 0; r < len; r++) {
                GlyphData rightGlyph = glyphs[r];
                if (rightGlyph == null) {
                    continue;
                }

                double area = calcAreaBetweenContours(
                        getContour(false, leftGlyph),
                        getContour(true, rightGlyph),
                        leftGlyph.getXMaximum()
                );

                kerningTable[l][r] = area;

                meanArea += area;
                counter++;
            }

            System.out.println(String.format("Kerning table for glyph row %d from %d done", l + 1, len));
        }

        return new OpticalKerningTable(meanArea / counter, kerningTable);
    }

    /**
     * Calculate the area between the passed right and left contour.
     *
     * @param right   contour
     * @param left    contour
     * @param xOffset the left contour is translated by
     * @return the area between the passed contours
     */
    private double calcAreaBetweenContours(List<Point> right, List<Point> left, short xOffset) {
        int maxY = Math.min(right.get(0).getY(), left.get(0).getY());
        int minY = Math.max(right.get(right.size() - 1).getY(), left.get(left.size() - 1).getY());

        class TrapeziumSide {
            int y;
            int xStart;
            int xEnd;
        }

        int rightIdx = 0;
        int leftIdx = 0;
        int y = maxY;
        List<TrapeziumSide> sides = new ArrayList<>();
        int xStart = Integer.MIN_VALUE;
        int xEnd = Integer.MIN_VALUE;
        while (y >= minY) {
            Point r1 = right.get(rightIdx);
            Point r2 = right.size() > rightIdx + 1 ? right.get(rightIdx + 1) : null;

            Point l1 = left.get(leftIdx);
            Point l2 = left.size() > leftIdx + 1 ? left.get(leftIdx + 1) : null;

            // Find start x coordinate at the currenty y-offset
            if (xStart == Integer.MIN_VALUE) {
                if (r1.getY() == y) {
                    xStart = r1.getX();
                } else if (r1.getY() > y && r2 != null && r2.getY() < y) {
                    // xStart is between r1 and r2 -> calculate
                    int xDiff = r1.getX() - r2.getX();
                    int yDiff = r1.getY() - r2.getY();

                    if (xDiff != 0) {
                        // Calculate slope
                        double slope = (double) yDiff / xDiff;

                        // Calculate t (y-axis section)
                        double t = r1.getY() - slope * r1.getX();

                        // Calculate xStart
                        xStart = (short) Math.round((y - t) / slope);
                    } else {
                        xStart = r1.getX();
                    }
                } else {
                    rightIdx++;
                }
            }

            // Find end x coordinate at the currenty y-offset
            if (xEnd == Integer.MIN_VALUE) {
                if (l1.getY() == y) {
                    xEnd = l1.getX() + xOffset;
                } else if (l1.getY() > y && l2 != null && l2.getY() < y) {
                    // xEnd is between l1 and l2 -> calculate
                    int xDiff = l1.getX() - l2.getX();
                    int yDiff = l1.getY() - l2.getY();

                    if (xDiff != 0) {
                        // Calculate slope
                        double slope = (double) yDiff / xDiff;

                        // Calculate t (y-axis section)
                        double t = l1.getY() - slope * l1.getX();

                        // Calculate xEnd
                        xEnd = (short) Math.round((y - t) / slope) + xOffset;
                    } else {
                        xEnd = l1.getX() + xOffset;
                    }
                } else {
                    leftIdx++;
                }
            }

            if (xStart != Integer.MIN_VALUE && xEnd != Integer.MIN_VALUE) {
                TrapeziumSide side = new TrapeziumSide();
                side.y = y;
                side.xStart = xStart;
                side.xEnd = xEnd;

                sides.add(side);

                // Choose next y
                int newY = Math.max(r2 != null ? r2.getY() : minY - 1, l2 != null ? l2.getY() : minY - 1);
                if (newY == y) {
                    y = newY - 1;
                } else {
                    y = newY;
                }

                // Reset x start and end
                xStart = Integer.MIN_VALUE;
                xEnd = Integer.MIN_VALUE;
            }
        }

        // Calculate area for each trapezium
        double area = 0;
        for (int i = 0; i < sides.size() - 1; i++) {
            TrapeziumSide side1 = sides.get(i);
            TrapeziumSide side2 = sides.get(i + 1);

            double height = side1.y - side2.y;
            int a = side1.xEnd - side1.xStart;
            int c = side2.xEnd - side2.xStart;

            area += height * (a + c) / 2;
        }

        return area;
    }

    private static class Point {
        private final int idx;
        private final int predecessorIdx;
        private final int successorIdx;
        private final short x;
        private final short y;

        public Point(int idx, int predecessorIdx, int successorIdx, short x, short y) {
            this.idx = idx;
            this.x = x;
            this.y = y;
            this.predecessorIdx = predecessorIdx;
            this.successorIdx = successorIdx;
        }

        public int getIdx() {
            return idx;
        }

        public int getPredecessorIdx() {
            return predecessorIdx;
        }

        public int getSuccessorIdx() {
            return successorIdx;
        }

        public short getX() {
            return x;
        }

        public short getY() {
            return y;
        }
    }

    /**
     * Get the right or left contour of the passed glyph data.
     *
     * @param left      or right contour
     * @param glyphData the glyph data
     * @return the left or right contour
     */
    private List<Point> getContour(boolean left, GlyphData glyphData) {
        GlyphDescription description = glyphData.getDescription();

        int contourCount = description.getContourCount();


        // TODO - Remove below

//        {
//            double startX = glyphData.getXMinimum();
//            double startY = glyphData.getYMinimum();
//            double width = glyphData.getBoundingBox().getWidth();
//            double height = glyphData.getBoundingBox().getHeight();
//
//            StringBuilder sb = new StringBuilder();
//            sb.append(String.format("<svg width='%f' height='%f'>", width, height));
//
//            int start = 0;
//            for (int c = 0; c < contourCount; c++) {
//                int end = description.getEndPtOfContours(c);
//
//                sb.append("<path d='");
//                for (int i = start; i <= end; i++) {
//                    short x = description.getXCoordinate(i);
//                    short y = description.getYCoordinate(i);
//
//                    if (i == start) {
//                        sb.append("M ");
//                    } else {
//                        sb.append(" L ");
//                    }
//
//                    sb.append(String.format("%d %d", (int) (x - startX), (int) (height - y + startY)));
//                }
//                sb.append(" Z' stroke-width='5' fill='none' stroke='blue' />");
//
//                start = end + 1;
//            }
//            sb.append("</svg>");
//
//            System.out.println(sb.toString());
//        }

        // TODO - REMOVE ABOVE


        class Range {
            private final short start;
            private final short end;

            public Range(short start, short end) {
                this.start = start;
                this.end = end;
            }

            public short getStart() {
                return start;
            }

            public short getEnd() {
                return end;
            }
        }

        int start = 0;
        List<List<Point>> countourPoints = new ArrayList<>();
        int bestContourPointsStartIdx = 0;
        short bestContourX = left ? Short.MAX_VALUE : Short.MIN_VALUE;
        for (int c = 0; c < contourCount; c++) {
            int end = description.getEndPtOfContours(c);

            List<Point> points = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                short x = description.getXCoordinate(i);
                short y = description.getYCoordinate(i);

                if (left) {
                    if (x < bestContourX) {
                        bestContourPointsStartIdx = c;
                        bestContourX = x;
                    }
                } else {
                    if (x > bestContourX) {
                        bestContourPointsStartIdx = c;
                        bestContourX = x;
                    }
                }

                int predecessor = i == start ? -1 : i - 1;
                int successor = i == end ? -1 : i + 1;

                points.add(new Point(i, predecessor, successor, x, y));
            }

            // Sort points by their x-coordinate
            if (left) {
                points.sort(Comparator.comparingInt(Point::getX));
            } else {
                points.sort(Comparator.comparingInt(Point::getX).reversed());
            }

            countourPoints.add(points);

            start = end + 1;
        }

        List<Point> points = countourPoints.get(bestContourPointsStartIdx);

        Map<Integer, Point> pointLookupByIdx = new HashMap<>();
        for (Point p : points) {
            pointLookupByIdx.put(p.getIdx(), p);
        }

        // Iterate from best x-coordinate to worst
        List<Point> contour = new ArrayList<>();
        List<Range> acceptedRanges = new ArrayList<>();
        Set<Integer> acceptedPoints = new HashSet<>();
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);

            boolean isInAcceptedRange = false;
            for (Range range : acceptedRanges) {
                if (point.getY() >= range.getStart() && point.getY() <= range.getEnd()) {
                    isInAcceptedRange = true;
                    break;
                }
            }

            if (!isInAcceptedRange) {
                // Accept the point as point of the contour
                contour.add(point);
                acceptedPoints.add(point.getIdx());
            }

            // Add the accepted range
            if (point.getSuccessorIdx() != -1 && acceptedPoints.contains(point.getSuccessorIdx())) {
                Point connectedPoint = pointLookupByIdx.get(point.getSuccessorIdx());
                acceptedRanges.add(new Range(
                        (short) Math.min(point.getY(), connectedPoint.getY()),
                        (short) Math.max(point.getY(), connectedPoint.getY())
                ));
            }
            if (point.getPredecessorIdx() != -1 && acceptedPoints.contains(point.getPredecessorIdx())) {
                Point connectedPoint = pointLookupByIdx.get(point.getPredecessorIdx());
                acceptedRanges.add(new Range(
                        (short) Math.min(point.getY(), connectedPoint.getY()),
                        (short) Math.max(point.getY(), connectedPoint.getY())
                ));
            }
        }

        contour.sort(Comparator.comparingInt(Point::getY).reversed());

        // TODO Remove below

//        {
//            double startX = glyphData.getXMinimum();
//            double startY = glyphData.getYMinimum();
//            double width = glyphData.getBoundingBox().getWidth();
//            double height = glyphData.getBoundingBox().getHeight();
//
//            StringBuilder sb = new StringBuilder();
//            sb.setLength(0);
//            sb.append(String.format("<svg width='%f' height='%f'><path d='", width, height));
//
//            boolean isFirst = true;
//            for (Point p : contour) {
//                if (isFirst) {
//                    isFirst = false;
//                    sb.append("M " + (p.getX() - startX) + " " + (height - p.getY() + startY));
//                } else {
//                    sb.append(" L " + (p.getX() - startX) + " " + (height - p.getY() + startY));
//                }
//            }
//
//            sb.append("' stroke-width='5' fill='none' stroke='blue' /></svg>");
//            System.out.println(sb.toString());
//        }

        // TODO Remove above

        return contour;
    }

}
