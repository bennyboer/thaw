package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.FractionElement;
import de.be.thaw.math.mathml.typeset.element.impl.RootElement;
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

        try {
            // Reset colors
            out.setStrokingColor(0.0f, 0.0f, 0.0f);
            out.setNonStrokingColor(0.0f, 0.0f, 0.0f);
        } catch (IOException e) {
            throw new ExportException(e);
        }

        renderElement(mee.getExpression().getRoot(), mee, ctx, out, y + mee.getExpression().getRoot().getSize().getHeight(), x);
    }

    /**
     * Render the passed elemnt.
     *
     * @param element to render
     * @param mee     the math expression element the passed element is part of
     * @param ctx     the export context
     * @param out     the output stream to the PDF document
     * @param yStart  current y offset on the document
     * @param xStart  current x offset on the document
     * @throws ExportException in case the element could not be rendered properly
     */
    private void renderElement(MathElement element, MathExpressionElement mee, ExportContext ctx, PDPageContentStream out, double yStart, double xStart) throws ExportException {
        try {
            double y = yStart - element.getPosition().getY();
            double x = xStart + element.getPosition().getX();

            if (element instanceof TokenElement) {
                showText((TokenElement) element, ctx, out, y, x);
            } else if (element instanceof FractionElement) {
                showFractionLine((FractionElement) element, ctx, out, y, x);
            } else if (element instanceof RootElement) {
                showRootLine((RootElement) element, ctx, out, y, x, mee.getNode().orElseThrow());
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }

        // Render children as well
        Optional<List<MathElement>> children = element.getChildren();
        if (children.isPresent()) {
            for (MathElement child : children.get()) {
                renderElement(child, mee, ctx, out, yStart, xStart);
            }
        }
    }

    /**
     * Show a root line.
     *
     * @param element      to show line for
     * @param ctx          the export context
     * @param out          the output stream
     * @param y            the y-offset
     * @param x            the x-offset
     * @param documentNode the element originally belongs to
     * @throws IOException in case the fraction line could not be shown properly
     */
    private void showRootLine(RootElement element, ExportContext ctx, PDPageContentStream out, double y, double x, DocumentNode documentNode) throws IOException {
        MathElement basis = element.getChildren().orElseThrow().get(0);
        MathElement exponent = element.getChildren().orElseThrow().get(1);

        // Prepare line drawing
        double fontSize = ctx.getFontSizeForNode(documentNode);
        double rootLineWidth = fontSize * 0.05;

        double basisX = basis.getPosition().getX() - element.getPosition().getX();
        double exponentY = exponent.getPosition().getY() - element.getPosition().getY();

        // Y position of the roots "hook"
        double startY = y - exponentY - exponent.getSize().getHeight() - fontSize / 10;
        double endY = y - element.getSize().getHeight();

        // Y position of the main line over the basis element of the root
        double mainRootLineY = y - rootLineWidth / 2;

        out.setLineJoinStyle(1);
        out.setLineCapStyle(0);

        // Draw hook start
        out.setLineWidth((float) rootLineWidth);

        out.moveTo((float) x, (float) (startY - (fontSize / 30)));
        out.lineTo((float) (x + basisX * 0.3), (float) startY);

        // Draw hook end
        out.lineTo((float) (x + basisX * 0.6), (float) endY);

        // Draw root line start
        out.lineTo((float) (x + basisX), (float) mainRootLineY);

        // Draw root line end (above the basis element)
        out.lineTo((float) (x + element.getSize().getWidth()), (float) mainRootLineY);

        // Draw root line end hook
        out.lineTo((float) (x + element.getSize().getWidth()), (float) (mainRootLineY - fontSize / 6));

        out.stroke();
    }

    /**
     * Show a fraction line.
     *
     * @param element to show line for
     * @param ctx     the export context
     * @param out     the output stream
     * @param y       the y-offset
     * @param x       the x-offset
     * @throws IOException in case the fraction line could not be shown properly
     */
    private void showFractionLine(FractionElement element, ExportContext ctx, PDPageContentStream out, double y, double x) throws IOException {
        MathElement numerator = element.getChildren().orElseThrow().get(0);

        // Draw fraction line
        out.setLineCapStyle(1);
        out.setLineWidth((float) element.getLineWidth());

        double lineY = y - numerator.getSize().getHeight() - element.getLineSpacing() - element.getLineWidth() / 2;
        double margin = element.getLineSpacing() / 2;

        out.moveTo((float) (x + margin), (float) lineY);
        out.lineTo((float) (x + element.getSize().getWidth() - margin), (float) lineY);
        out.stroke();
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
