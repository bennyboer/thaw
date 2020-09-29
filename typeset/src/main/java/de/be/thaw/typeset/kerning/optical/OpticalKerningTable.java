package de.be.thaw.typeset.kerning.optical;

import de.be.thaw.typeset.kerning.KerningTable;
import de.be.thaw.typeset.kerning.glyph.Coordinate;
import de.be.thaw.typeset.kerning.glyph.Glyph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Kerning table trying to kern character pairs optically.
 */
public class OpticalKerningTable implements KerningTable, Serializable {

    /**
     * The mean minimum distance between optical kernable character pairs.
     */
    private double meanMinDistance;

    /**
     * The mean area between optical kernable character pairs.
     */
    private double meanArea;

    /**
     * Height of the mean area between optical kernable character pairs.
     */
    private double meanAreaHeight;

    /**
     * The lookup for kerning per kerning pair.
     */
    private final Map<KerningPair, KerningValue> kerningLookup = new HashMap<>();

    @Override
    public void init(Glyph[] glyphs) {
        kerningLookup.clear();

        double meanMinDistanceSum = 0;
        double meanAreaSum = 0;
        double meanAreaHeightSum = 0;

        long counter = 0;

        for (Glyph leftGlyph : glyphs) {
            for (Glyph rightGlyph : glyphs) {
                AreaCalculationResult result = calcAreaBetweenContours(
                        getContour(false, leftGlyph),
                        getContour(true, rightGlyph),
                        leftGlyph.getSize().getWidth() + leftGlyph.getPosition().getX()
                );

                kerningLookup.put(
                        new KerningPair(leftGlyph.getGlyphID(), rightGlyph.getGlyphID()),
                        new KerningValue(leftGlyph, rightGlyph, result)
                );

                meanMinDistanceSum += result.getMinDistance();
                meanAreaSum += result.getArea();
                meanAreaHeightSum += result.getAreaHeight();

                counter++;
            }
        }

        meanMinDistance = meanMinDistanceSum / counter;
        meanArea = meanAreaSum / counter;
        meanAreaHeight = meanAreaHeightSum / counter;
    }

    @Override
    public double getKerning(int leftGlyphID, int rightGlyphID) {
        // Fetch the kerning pair
        KerningValue value = kerningLookup.get(new KerningPair(leftGlyphID, rightGlyphID));
        if (value == null) {
            return 0; // No kerning
        }

        double deviation = Math.abs(meanMinDistance - value.getAreaCalculationResult().getMinDistance());
        double relativeDeviation = deviation / meanMinDistance;

        if (relativeDeviation > 0.2) { // Only correct bad situations
            return -value.getAreaCalculationResult().getMinDistance() * Math.min(0.4, relativeDeviation); // Do kerning!
        } else {
            return 0; // No kerning needed
        }
    }

    /**
     * Calculate the area between the passed right and left contour.
     *
     * @param right   contour
     * @param left    contour
     * @param xOffset the left contour is translated by
     * @return the area between the passed contours
     */
    private AreaCalculationResult calcAreaBetweenContours(List<Coordinate> right, List<Coordinate> left, double xOffset) {
        if (right.isEmpty() || left.isEmpty()) {
            return new AreaCalculationResult(0, 0, 0, 0, 0);
        }

        double maxY = Math.min(right.get(0).getY(), left.get(0).getY());
        double minY = Math.max(right.get(right.size() - 1).getY(), left.get(left.size() - 1).getY());

        class TrapeziumSide {
            double y;
            double xStart;
            double xEnd;
        }

        int rightIdx = 0;
        int leftIdx = 0;

        List<TrapeziumSide> sides = new ArrayList<>();

        double xStart = Integer.MIN_VALUE;
        double xEnd = Integer.MIN_VALUE;

        double y = maxY;
        while (y >= minY) {
            Coordinate r1 = right.get(rightIdx);
            Coordinate r2 = right.size() > rightIdx + 1 ? right.get(rightIdx + 1) : null;

            Coordinate l1 = left.get(leftIdx);
            Coordinate l2 = left.size() > leftIdx + 1 ? left.get(leftIdx + 1) : null;

            // Find start x coordinate at the currenty y-offset
            if (xStart == Integer.MIN_VALUE) {
                if (r1.getY() == y) {
                    xStart = r1.getX();
                } else if (r1.getY() > y && r2 != null && r2.getY() < y) {
                    // xStart is between r1 and r2 -> calculate
                    double xDiff = r1.getX() - r2.getX();
                    double yDiff = r1.getY() - r2.getY();

                    if (xDiff != 0) {
                        // Calculate slope
                        double slope = (double) yDiff / xDiff;

                        // Calculate t (y-axis section)
                        double t = r1.getY() - slope * r1.getX();

                        // Calculate xStart
                        xStart = Math.round((y - t) / slope);
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
                    double xDiff = l1.getX() - l2.getX();
                    double yDiff = l1.getY() - l2.getY();

                    if (xDiff != 0) {
                        // Calculate slope
                        double slope = (double) yDiff / xDiff;

                        // Calculate t (y-axis section)
                        double t = l1.getY() - slope * l1.getX();

                        // Calculate xEnd
                        xEnd = Math.round((y - t) / slope) + xOffset;
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
                double newY = Math.max(r2 != null ? r2.getY() : minY - 1, l2 != null ? l2.getY() : minY - 1);
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
            double a = side1.xEnd - side1.xStart;
            double c = side2.xEnd - side2.xStart;

            area += height * (a + c) / 2;
        }

        // Calculate other metrics
        double minDistance = Double.POSITIVE_INFINITY;
        double distanceSum = 0;
        double maxDistance = Double.NEGATIVE_INFINITY;
        for (TrapeziumSide side : sides) {
            double distance = side.xEnd - side.xStart;

            minDistance = Math.min(minDistance, distance);
            maxDistance = Math.max(maxDistance, distance);
            distanceSum += distance;
        }

        return new AreaCalculationResult(
                area,
                maxY - minY,
                Double.isInfinite(minDistance) ? 0 : minDistance,
                sides.size() > 0 ? distanceSum / sides.size() : 0,
                Double.isInfinite(minDistance) ? 0 : maxDistance
        );
    }

    /**
     * Get the right or left contour of the passed glyph data.
     *
     * @param left  or right contour
     * @param glyph the glyph to get contour for
     * @return the left or right contour
     */
    private List<Coordinate> getContour(boolean left, Glyph glyph) {
        class Point {
            private final int idx;
            private final int predecessorIdx;
            private final int successorIdx;
            private final double x;
            private final double y;

            public Point(int idx, int predecessorIdx, int successorIdx, double x, double y) {
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

            public double getX() {
                return x;
            }

            public double getY() {
                return y;
            }
        }

        class Range {
            private final double start;
            private final double end;

            public Range(double start, double end) {
                this.start = start;
                this.end = end;
            }

            public double getStart() {
                return start;
            }

            public double getEnd() {
                return end;
            }
        }

        // Find the best contour (righter or lefter most) to use of the glyph
        List<Point> points = new ArrayList<>();
        int pointIndex = 0;
        int maxPointIndex = -1;
        for (List<Coordinate> contour : glyph.getContours()) {
            maxPointIndex += contour.size();
        }

        for (int a = 0; a < glyph.getContours().size(); a++) {
            List<Coordinate> contour = glyph.getContours().get(a);

            for (Coordinate coordinate : contour) {
                int predecessor = pointIndex == 0 ? -1 : pointIndex - 1;
                int successor = pointIndex == maxPointIndex ? -1 : pointIndex + 1;

                points.add(new Point(pointIndex, predecessor, successor, coordinate.getX(), coordinate.getY()));

                pointIndex++;
            }
        }

        // Sort points by their x-coordinate
        if (left) {
            points.sort(Comparator.comparingDouble(Point::getX));
        } else {
            points.sort(Comparator.comparingDouble(Point::getX).reversed());
        }

        Map<Integer, Point> pointLookupByIdx = new HashMap<>();
        for (Point p : points) {
            pointLookupByIdx.put(p.getIdx(), p);
        }

        // Iterate from best x-coordinate to worst
        List<Point> contour = new ArrayList<>();
        List<Range> acceptedRanges = new ArrayList<>();
        Set<Integer> acceptedPoints = new HashSet<>();
        for (Point point : points) {
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
                        Math.min(point.getY(), connectedPoint.getY()),
                        Math.max(point.getY(), connectedPoint.getY())
                ));
            }
            if (point.getPredecessorIdx() != -1 && acceptedPoints.contains(point.getPredecessorIdx())) {
                Point connectedPoint = pointLookupByIdx.get(point.getPredecessorIdx());
                acceptedRanges.add(new Range(
                        Math.min(point.getY(), connectedPoint.getY()),
                        Math.max(point.getY(), connectedPoint.getY())
                ));
            }
        }

        contour.sort(Comparator.comparingDouble(Point::getY).reversed());

        return contour.stream().map(p -> new Coordinate(p.getX(), p.getY())).collect(Collectors.toList());
    }

    /**
     * A kerning pair.
     */
    private static class KerningPair implements Serializable {

        /**
         * The left glyph ID.
         */
        private final int left;

        /**
         * The right glyph ID.
         */
        private final int right;

        public KerningPair(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public int getLeft() {
            return left;
        }

        public int getRight() {
            return right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            KerningPair that = (KerningPair) o;

            if (left != that.left) return false;
            return right == that.right;
        }

        @Override
        public int hashCode() {
            int result = left;
            result = 31 * result + right;
            return result;
        }

    }

    /**
     * Value specifying the kerning as well as additional glyph information.
     */
    private static class KerningValue implements Serializable {

        /**
         * The left glyph.
         */
        private final Glyph left;

        /**
         * The right glyph.
         */
        private final Glyph right;

        /**
         * Result of the area calculation.
         */
        private final AreaCalculationResult areaCalculationResult;

        public KerningValue(Glyph left, Glyph right, AreaCalculationResult areaCalculationResult) {
            this.left = left;
            this.right = right;
            this.areaCalculationResult = areaCalculationResult;
        }

        public Glyph getLeft() {
            return left;
        }

        public Glyph getRight() {
            return right;
        }

        public AreaCalculationResult getAreaCalculationResult() {
            return areaCalculationResult;
        }

    }

    /**
     * Result of the area calculation.
     */
    private static class AreaCalculationResult implements Serializable {

        /**
         * The calculated area.
         */
        private final double area;

        /**
         * Height of the calculated area.
         */
        private final double areaHeight;

        /**
         * The minimum x distance.
         */
        private final double minDistance;

        /**
         * The mean x distance.
         */
        private final double meanDistance;

        /**
         * The maximum x distance.
         */
        private final double maxDistance;

        public AreaCalculationResult(double area, double areaHeight, double minDistance, double meanDistance, double maxDistance) {
            this.area = area;
            this.areaHeight = areaHeight;
            this.minDistance = minDistance;
            this.meanDistance = meanDistance;
            this.maxDistance = maxDistance;
        }

        public double getArea() {
            return area;
        }

        public double getAreaHeight() {
            return areaHeight;
        }

        public double getMinDistance() {
            return minDistance;
        }

        public double getMeanDistance() {
            return meanDistance;
        }

        public double getMaxDistance() {
            return maxDistance;
        }

    }

}
