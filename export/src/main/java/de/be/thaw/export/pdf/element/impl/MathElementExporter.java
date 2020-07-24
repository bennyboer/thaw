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
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.MathExpressionElement;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
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

        double fontSize = ctx.getFontSizeForNode(element.getNode().orElseThrow());

        renderElement(mee.getExpression().getRoot(), ctx, out, fontSize, y, x);
    }

    /**
     * Render the passed elemnt.
     *
     * @param element  to render
     * @param ctx      the export context
     * @param out      the output stream to the PDF document
     * @param fontSize to render with
     * @param y        current y offset on the document
     * @param x        current x offset on the document
     * @throws ExportException in case the element could not be rendered properly
     */
    private void renderElement(MathElement element, ExportContext ctx, PDPageContentStream out, double fontSize, double y, double x) throws ExportException {
        try {
            if (element instanceof IdentifierElement) {
                showText(((IdentifierElement) element).getIdentifier(), ctx, out, fontSize, y - element.getPosition().getY(), x + element.getPosition().getX());
            } else if (element instanceof OperatorElement) {
                showText(((OperatorElement) element).getOperator(), ctx, out, fontSize, y - element.getPosition().getY(), x + element.getPosition().getX());
            } else if (element instanceof NumericElement) {
                showText(((NumericElement) element).getValue(), ctx, out, fontSize, y - element.getPosition().getY(), x + element.getPosition().getX());
            } else if (element instanceof FractionElement) {
                // TODO
                System.out.println("Should show fraction line...");
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }

        // Render children as well
        Optional<List<MathElement>> children = element.getChildren();
        if (children.isPresent()) {
            for (MathElement child : children.get()) {
                renderElement(child, ctx, out, fontSize, y, x);
            }
        }
    }

    /**
     * Show the passed text on the document.
     *
     * @param str      to show
     * @param ctx      the export context
     * @param out      the output stream to the PDF document
     * @param fontSize to render with
     * @param y        current y offset on the document
     * @param x        current x offset on the document
     * @throws IOException in case the string could not be shown properly
     */
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
