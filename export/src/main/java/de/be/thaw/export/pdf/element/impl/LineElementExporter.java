package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.LineElement;
import de.be.thaw.typeset.page.util.LineStyle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.Set;

/**
 * Exporter for lines (for example a border line, horizontal line, ...).
 */
public class LineElementExporter implements ElementExporter {

    /**
     * Supported element types by this exporter.
     */
    private static final Set<ElementType> SUPPORTED_TYPES = Set.of(ElementType.LINE);

    @Override
    public Set<ElementType> supportedElementTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public void export(Element element, ExportContext ctx) throws ExportException {
        LineElement lineElement = (LineElement) element;

        double yStart = ctx.getCurrentPage().getMediaBox().getUpperRightY() - lineElement.getPosition().getY();
        double yEnd = yStart - lineElement.getSize().getHeight();

        try {
            PDPageContentStream out = ctx.getContentStream();
            out.saveGraphicsState();

            out.setLineJoinStyle(0);
            out.setLineCapStyle(0);

            out.setStrokingColor(
                    (float) lineElement.getColor().getRed(),
                    (float) lineElement.getColor().getGreen(),
                    (float) lineElement.getColor().getBlue()
            );

            if (lineElement.getStyle() == LineStyle.DOTTED) {
                out.setLineDashPattern(new float[]{(float) lineElement.getLineWidth(), 3.0f}, 0);
            }

            out.setLineWidth((float) lineElement.getLineWidth());

            out.moveTo((float) lineElement.getPosition().getX(), (float) yStart);
            out.lineTo((float) (lineElement.getPosition().getX() + lineElement.getSize().getWidth()), (float) yEnd);

            out.stroke();

            out.restoreGraphicsState();
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    @Override
    public void afterExport(Element element, ExportContext ctx) throws ExportException {
        // Nothing to do after export
    }

}
