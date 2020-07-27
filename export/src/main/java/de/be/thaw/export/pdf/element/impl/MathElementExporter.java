package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.FractionElement;
import de.be.thaw.math.mathml.typeset.element.impl.IdentifierElement;
import de.be.thaw.math.mathml.typeset.element.impl.NumericElement;
import de.be.thaw.math.mathml.typeset.element.impl.OperatorElement;
import de.be.thaw.math.mathml.typeset.element.impl.TextElement;
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
            if (element instanceof IdentifierElement) {
                showText(((IdentifierElement) element).getIdentifier(), element, ctx, out, ((IdentifierElement) element).getKerningAdjustments(), ((IdentifierElement) element).getFontSize(), yStart - element.getPosition().getY(), xStart + element.getPosition().getX());
            } else if (element instanceof OperatorElement) {
                showText(((OperatorElement) element).getOperator(), element, ctx, out, ((OperatorElement) element).getKerningAdjustments(), ((OperatorElement) element).getFontSize(), yStart - element.getPosition().getY(), xStart + element.getPosition().getX());
            } else if (element instanceof NumericElement) {
                showText(((NumericElement) element).getValue(), element, ctx, out, ((NumericElement) element).getKerningAdjustments(), ((NumericElement) element).getFontSize(), yStart - element.getPosition().getY(), xStart + element.getPosition().getX());
            } else if (element instanceof TextElement) {
                showText(((TextElement) element).getText(), element, ctx, out, ((TextElement) element).getKerningAdjustments(), ((TextElement) element).getFontSize(), yStart - element.getPosition().getY(), xStart + element.getPosition().getX());
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
     * @param str                to show
     * @param element            the string belongs to
     * @param ctx                the export context
     * @param out                the output stream to the PDF document
     * @param kerningAdjustments the kerning adjustments
     * @param fontSize           to render with
     * @param y                  current y offset on the document
     * @param x                  current x offset on the document
     * @throws IOException in case the string could not be shown properly
     */
    private void showText(String str, MathElement element, ExportContext ctx, PDPageContentStream out, double[] kerningAdjustments, double fontSize, double y, double x) throws IOException {
        out.beginText();

        ThawPdfFont font = (ThawPdfFont) ctx.getMathFont();

        // Apply font
        out.setFont(font.getPdFont(), (float) fontSize);

        // Set the position of where to draw the text
        out.newLineAtOffset((float) x, (float) (y - element.getSize().getHeight()));

        if (kerningAdjustments != null) {
            showTextWithKerning(str, element, ctx, out, kerningAdjustments, fontSize, y, x);
        } else {
            out.showText(str);
        }

        out.endText();
    }

    /**
     * Show the passed text for the given font with kerning adjustments applied.
     *
     * @param text               to show
     * @param element            the string belongs to
     * @param ctx                the export context
     * @param out                the output stream to the PDF document
     * @param kerningAdjustments the kerning adjustments
     * @param fontSize           to render with
     * @param y                  current y offset on the document
     * @param x                  current x offset on the document
     * @throws IOException in case the string could not be shown properly
     */
    private void showTextWithKerning(String text, MathElement element, ExportContext ctx, PDPageContentStream out, double[] kerningAdjustments, double fontSize, double y, double x) throws IOException {
        List<Object> toPrint = new ArrayList<>();

        int codePointIdx = 0;
        final int len = text.length();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < len; ) {
            int codePoint = text.codePointAt(i);
            i += Character.charCount(codePoint);

            double kerningAdjustment = kerningAdjustments[codePointIdx];
            if (kerningAdjustment != 0) {
                toPrint.add(buffer.toString());
                buffer.setLength(0);

                double adjustment = kerningAdjustment * 1000 / fontSize;
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
