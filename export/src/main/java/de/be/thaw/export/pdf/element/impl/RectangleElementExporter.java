package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.RectangleElement;
import de.be.thaw.typeset.page.util.LineStyle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.Set;

/**
 * Exporter for rectangles.
 */
public class RectangleElementExporter implements ElementExporter {

    /**
     * Supported element types by this exporter.
     */
    private static final Set<ElementType> SUPPORTED_TYPES = Set.of(ElementType.RECTANGLE);

    @Override
    public Set<ElementType> supportedElementTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public void export(Element element, ExportContext ctx) throws ExportException {
        RectangleElement rect = (RectangleElement) element;

        double yEnd = ctx.getCurrentPage().getMediaBox().getUpperRightY() - rect.getPosition().getY();
        double yStart = yEnd - rect.getSize().getHeight();

        double xStart = rect.getPosition().getX();
        double xEnd = xStart + rect.getSize().getWidth();

        // Check if all border sides have the same properties
        boolean sameWidths = rect.getBorderWidths().getTop() == rect.getBorderWidths().getBottom()
                && rect.getBorderWidths().getRight() == rect.getBorderWidths().getBottom()
                && rect.getBorderWidths().getLeft() == rect.getBorderWidths().getBottom();
        boolean sameColors = rect.getBottomStrokeColor().equals(rect.getTopStrokeColor())
                && rect.getLeftStrokeColor().equals(rect.getTopStrokeColor())
                && rect.getRightStrokeColor().equals(rect.getTopStrokeColor());
        boolean sameStyles = rect.getBottomBorderStyle() == rect.getTopBorderStyle()
                && rect.getLeftBorderStyle() == rect.getTopBorderStyle()
                && rect.getRightBorderStyle() == rect.getTopBorderStyle();
        boolean strokeAround = sameWidths && sameColors && sameStyles;

        try {
            PDPageContentStream out = ctx.getContentStream();
            out.saveGraphicsState();

            out.setNonStrokingColor(
                    (float) rect.getFillColor().getRed(),
                    (float) rect.getFillColor().getGreen(),
                    (float) rect.getFillColor().getBlue()
            );

            // First and foremost define the clipping area for the rectangle
            doRectPath(out, xStart, xEnd, yStart, yEnd, rect);
            out.clip();

            // Now fill the clipping area
            out.moveTo((float) xStart, (float) yStart);
            out.lineTo((float) xEnd, (float) yStart);
            out.lineTo((float) xEnd, (float) yEnd);
            out.lineTo((float) xStart, (float) yEnd);

            out.fill();

            // Now stroke the border sides (if any)
            if (strokeAround) {
                if (rect.getBorderWidths().getTop() > 0) {
                    out.setStrokingColor(
                            (float) rect.getStrokeColors()[0].getRed(),
                            (float) rect.getStrokeColors()[0].getGreen(),
                            (float) rect.getStrokeColors()[0].getBlue()
                    );
                    out.setLineWidth((float) rect.getBorderWidths().getTop());
                    if (rect.getBorderStyles()[0] == LineStyle.DOTTED) {
                        out.setLineDashPattern(new float[]{(float) rect.getBorderWidths().getTop(), 3.0f}, 0);
                    }

                    doRectPath(out, xStart, xEnd, yStart, yEnd, rect);
                    out.stroke();
                }
            } else {
                // Right side
                if (rect.getBorderWidths().getRight() > 0) {
                    out.setStrokingColor(
                            (float) rect.getStrokeColors()[1].getRed(),
                            (float) rect.getStrokeColors()[1].getGreen(),
                            (float) rect.getStrokeColors()[1].getBlue()
                    );
                    out.setLineWidth((float) rect.getBorderWidths().getRight());
                    if (rect.getBorderStyles()[1] == LineStyle.DOTTED) {
                        out.setLineDashPattern(new float[]{(float) rect.getBorderWidths().getRight(), 3.0f}, 0);
                    }
                    out.moveTo((float) xEnd, (float) yStart);
                    out.lineTo((float) xEnd, (float) yEnd);

                    out.stroke();
                }

                // Left side
                if (rect.getBorderWidths().getLeft() > 0) {
                    out.setStrokingColor(
                            (float) rect.getStrokeColors()[3].getRed(),
                            (float) rect.getStrokeColors()[3].getGreen(),
                            (float) rect.getStrokeColors()[3].getBlue()
                    );
                    out.setLineWidth((float) rect.getBorderWidths().getLeft());
                    if (rect.getBorderStyles()[3] == LineStyle.DOTTED) {
                        out.setLineDashPattern(new float[]{(float) rect.getBorderWidths().getLeft(), 3.0f}, 0);
                    }
                    out.moveTo((float) xStart, (float) yEnd);
                    out.lineTo((float) xStart, (float) yStart);

                    out.stroke();
                }

                // Top side
                if (rect.getBorderWidths().getTop() > 0) {
                    out.setStrokingColor(
                            (float) rect.getStrokeColors()[0].getRed(),
                            (float) rect.getStrokeColors()[0].getGreen(),
                            (float) rect.getStrokeColors()[0].getBlue()
                    );
                    out.setLineWidth((float) rect.getBorderWidths().getTop());
                    if (rect.getBorderStyles()[0] == LineStyle.DOTTED) {
                        out.setLineDashPattern(new float[]{(float) rect.getBorderWidths().getTop(), 3.0f}, 0);
                    }
                    out.moveTo((float) xEnd, (float) yEnd);
                    out.lineTo((float) xStart, (float) yEnd);

                    out.stroke();
                }

                // Bottom side
                if (rect.getBorderWidths().getBottom() > 0) {
                    out.setStrokingColor(
                            (float) rect.getStrokeColors()[2].getRed(),
                            (float) rect.getStrokeColors()[2].getGreen(),
                            (float) rect.getStrokeColors()[2].getBlue()
                    );
                    out.setLineWidth((float) rect.getBorderWidths().getBottom());
                    if (rect.getBorderStyles()[2] == LineStyle.DOTTED) {
                        out.setLineDashPattern(new float[]{(float) rect.getBorderWidths().getBottom(), 3.0f}, 0);
                    }
                    out.moveTo((float) xStart, (float) yStart);
                    out.lineTo((float) xEnd, (float) yStart);

                    out.stroke();
                }
            }

            out.restoreGraphicsState();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    /**
     * Do the rectangle path.
     *
     * @param out    stream to write path to
     * @param xStart the x start
     * @param xEnd   the x end
     * @param yStart the y start
     * @param yEnd   the y end
     * @param rect   the rectangle
     * @throws IOException in case the path could not be done
     */
    private void doRectPath(PDPageContentStream out, double xStart, double xEnd, double yStart, double yEnd, RectangleElement rect) throws IOException {
        // Top line (without border radius curves)
        out.moveTo((float) (xStart + rect.getBorderRadius().getTop()), (float) yEnd);
        out.lineTo((float) (xEnd - rect.getBorderRadius().getRight()), (float) yEnd);

        // Border radius curve to the top on the right
        if (rect.getBorderRadius().getRight() > 0) {
            out.curveTo1((float) xEnd, (float) yEnd, (float) xEnd, (float) (yEnd - rect.getBorderRadius().getRight()));
        }

        // Right line (without border radius curves)
        out.lineTo((float) xEnd, (float) (yStart + rect.getBorderRadius().getBottom()));

        // Border radius curve to the bottom on the right
        if (rect.getBorderRadius().getBottom() > 0) {
            out.curveTo2((float) xEnd, (float) yStart, (float) (xEnd - rect.getBorderRadius().getBottom()), (float) yStart);
        }

        // Bottom line (without border radius curves)
        out.lineTo((float) (xStart + rect.getBorderRadius().getLeft()), (float) yStart);

        // Border radius curve to the bottom on the left
        if (rect.getBorderRadius().getLeft() > 0) {
            out.curveTo1((float) xStart, (float) yStart, (float) xStart, (float) (yStart + rect.getBorderRadius().getLeft()));
        }

        // Left line (without border radius curves)
        out.lineTo((float) xStart, (float) (yEnd - rect.getBorderRadius().getTop()));

        // Border radius curve to the top on the left
        if (rect.getBorderRadius().getTop() > 0) {
            out.curveTo1((float) xStart, (float) yEnd, (float) (xStart + rect.getBorderRadius().getTop()), (float) yEnd);
        }
    }

    @Override
    public void afterExport(Element element, ExportContext ctx) throws ExportException {
        // Nothing to do after export
    }

}
