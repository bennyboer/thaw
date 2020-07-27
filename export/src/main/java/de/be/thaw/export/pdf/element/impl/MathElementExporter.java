package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.FractionElement;
import de.be.thaw.math.mathml.typeset.element.impl.TokenElement;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.MathExpressionElement;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Exporter for math expressions.
 */
public class MathElementExporter implements ElementExporter {

    /**
     * Supported element types by this exporter.
     */
    private static final Set<ElementType> SUPPORTED_TYPES = Set.of(ElementType.MATH);

    @Override
    public Set<ElementType> supportedElementTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public void export(Element element, ExportContext ctx) throws ExportException {
        MathExpressionElement mee = (MathExpressionElement) element;

        double y = ctx.getCurrentPage().getMediaBox().getUpperRightY() - mee.getPosition().getY() - element.getSize().getHeight();
        double x = mee.getPosition().getX();

        PDPageContentStream out = ctx.getContentStream();

        renderElement(mee.getExpression().getRoot(), ctx, out, y + mee.getExpression().getRoot().getSize().getHeight(), x);
    }

    /**
     * Render the passed elemnt.
     *
     * @param element to render
     * @param ctx     the export context
     * @param out     the output stream to the PDF document
     * @param yStart  current y offset on the document
     * @param xStart  current x offset on the document
     * @throws ExportException in case the element could not be rendered properly
     */
    private void renderElement(MathElement element, ExportContext ctx, PDPageContentStream out, double yStart, double xStart) throws ExportException {
        try {
            if (element instanceof TokenElement) {
                showText((TokenElement) element, ctx, out, yStart - element.getPosition().getY(), xStart + element.getPosition().getX());
            } else if (element instanceof FractionElement) {
                FractionElement fractionElement = (FractionElement) element;
                MathElement numerator = fractionElement.getChildren().orElseThrow().get(0);

                // Draw fraction line
                out.setLineWidth((float) ((FractionElement) element).getLineWidth());

                double lineY = yStart - element.getPosition().getY() - numerator.getSize().getHeight() - fractionElement.getLineSpacing();

                out.moveTo((float) (xStart + element.getPosition().getX()), (float) lineY);
                out.lineTo((float) (xStart + element.getPosition().getX() + element.getSize().getWidth()), (float) lineY);
                out.stroke();
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }

        // Render children as well
        Optional<List<MathElement>> children = element.getChildren();
        if (children.isPresent()) {
            for (MathElement child : children.get()) {
                renderElement(child, ctx, out, yStart, xStart);
            }
        }
    }

    /**
     * Show the passed text on the document.
     *
     * @param element the string belongs to
     * @param ctx     the export context
     * @param out     the output stream to the PDF document
     * @param y       current y offset on the document
     * @param x       current x offset on the document
     * @throws IOException in case the string could not be shown properly
     */
    private void showText(TokenElement element, ExportContext ctx, PDPageContentStream out, double y, double x) throws IOException {
        out.beginText();

        ThawPdfFont font = (ThawPdfFont) ctx.getMathFont();

        // Apply font
        out.setFont(font.getPdFont(), (float) element.getFontSize());

        // Set the position of where to draw the text
        out.newLineAtOffset((float) x, (float) (y - element.getBaseline()));

        if (element.getKerningAdjustments() != null) {
            showTextWithKerning(element, out);
        } else {
            out.showText(element.getText());
        }

        out.endText();
    }

    /**
     * Show the passed text for the given font with kerning adjustments applied.
     *
     * @param element the string belongs to
     * @param out     the output stream to the PDF document
     * @throws IOException in case the string could not be shown properly
     */
    private void showTextWithKerning(TokenElement element, PDPageContentStream out) throws IOException {
        List<Object> toPrint = new ArrayList<>();

        int codePointIdx = 0;
        final int len = element.getText().length();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < len; ) {
            int codePoint = element.getText().codePointAt(i);
            i += Character.charCount(codePoint);

            double kerningAdjustment = element.getKerningAdjustments()[codePointIdx];
            if (kerningAdjustment != 0) {
                toPrint.add(buffer.toString());
                buffer.setLength(0);

                double adjustment = kerningAdjustment * 1000 / element.getFontSize();
                toPrint.add((float) -adjustment);
            }

            buffer.append(Character.toChars(codePoint));

            codePointIdx++;
        }

        toPrint.add(buffer.toString());

        out.showTextWithPositioning(toPrint.toArray());
    }

    @Override
    public void afterExport(Element element, ExportContext ctx) throws ExportException {
        // Nothing to do after export
    }

}
