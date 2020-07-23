package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.IdentifierElement;
import de.be.thaw.math.mathml.typeset.element.impl.NumericElement;
import de.be.thaw.math.mathml.typeset.element.impl.OperatorElement;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.MathExpressionElement;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
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

        double fontSize = ctx.getFontSizeForNode(element.getNode().orElseThrow());

        try {
            for (MathElement elm : mee.getExpression().getElements()) {
                if (elm instanceof IdentifierElement) {
                    showText(((IdentifierElement) elm).getIdentifier(), ctx, out, fontSize, y + elm.getPosition().getY(), x + elm.getPosition().getX());
                } else if (elm instanceof OperatorElement) {
                    showText(((OperatorElement) elm).getOperator(), ctx, out, fontSize, y + elm.getPosition().getY(), x + elm.getPosition().getX());
                } else if (elm instanceof NumericElement) {
                    showText(((NumericElement) elm).getValue(), ctx, out, fontSize, y + elm.getPosition().getY(), x + elm.getPosition().getX());
                } else {
                    throw new ExportException(String.format(
                            "Could not export math element with class '%s' -> Not implemented",
                            elm.getClass().getSimpleName()
                    ));
                }
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    private void showText(String str, ExportContext ctx, PDPageContentStream out, double fontSize, double y, double x) throws IOException {
        out.beginText();

        ThawPdfFont font = (ThawPdfFont) ctx.getMathFont();

        // Apply font
        out.setFont(font.getPdFont(), (float) fontSize);

        // Set the position of where to draw the text
        out.newLineAtOffset((float) x, (float) (y + fontSize));

        out.showText(str);

        out.endText();
    }

    @Override
    public void afterExport(Element element, ExportContext ctx) throws ExportException {
        // Nothing to do after export
    }

}
