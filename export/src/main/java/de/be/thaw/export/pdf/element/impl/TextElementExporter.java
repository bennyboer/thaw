package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.ExportContext;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.TextElement;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TextElementExporter implements ElementExporter {

    /**
     * Supported element types by this exporter.
     */
    private static final Set<ElementType> SUPPORTED_TYPES = Set.of(ElementType.TEXT);

    @Override
    public Set<ElementType> supportedElementTypes() {
        return SUPPORTED_TYPES;
    }

    @Override
    public void export(Element element, ExportContext ctx) throws ExportException {
        TextElement te = (TextElement) element;

        PDPageContentStream out = ctx.getContentStream();

        try {
            out.beginText();

            ThawPdfFont font = (ThawPdfFont) ctx.getFontForNode(te.getNode());
            double fontSize = ctx.getFontSizeForNode(te.getNode());

            double y = ctx.getCurrentPage().getMediaBox().getUpperRightY() - te.getPosition().getY() - fontSize;

            // Apply font and size
            out.setFont(font.getPdFont(), (float) fontSize);
            out.newLineAtOffset((float) te.getPosition().getX(), (float) y);

            // Apply font color
            ColorStyle colorStyle = ((TextElement) element).getNode().getStyle().getStyleAttribute(
                    StyleType.FONT,
                    style -> Optional.ofNullable(((FontStyle) style).getColor())
            ).orElseThrow();
            out.setNonStrokingColor(new PDColor(
                    new float[]{colorStyle.getRed().floatValue(), colorStyle.getGreen().floatValue(), colorStyle.getBlue().floatValue()},
                    PDDeviceRGB.INSTANCE
            ));

            if (te.getKerningAdjustments() != null) {
                showTextWithKerning(out, font, te.getText(), te.getKerningAdjustments(), te.getFontSize());
            } else {
                out.showText(te.getText());
            }

            out.endText();

            underlineIfNecessary(te, ctx, out, font.getPdFont(), fontSize, y);
        } catch (IOException e) {
            throw new ExportException("Text element could not be exported to PDF due to another exception", e);
        }
    }

    /**
     * Show the passed text for the given font with kerning adjustments applied.
     *
     * @param out                to write text to
     * @param font               to use
     * @param text               to write
     * @param kerningAdjustments to apply
     * @param fontSize           of the text
     */
    private void showTextWithKerning(PDPageContentStream out, ThawPdfFont font, String text, double[] kerningAdjustments, double fontSize) throws IOException {
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

    private void underlineIfNecessary(TextElement element, ExportContext ctx, PDPageContentStream out, PDFont font, double fontSize, double y) throws IOException {
        if (element.getNode().getTextNode().getType() == NodeType.FORMATTED) {
            FormattedNode fn = (FormattedNode) element.getNode().getTextNode();

            if (fn.getEmphases().contains(TextEmphasis.UNDERLINED)) {
                double descent = font.getFontDescriptor().getDescent() / 1000 * fontSize;
                double lineWidth = Math.max(0.5, font.getFontDescriptor().getFontWeight() / 1000 * fontSize / 15);

                double lineStartX = element.getPosition().getX();
                double lineEndX = element.getPosition().getX() + element.getSize().getWidth();

                // Check if previous element has also been underlined
                if (ctx.isCurrentlyUnderlined()) {
                    if (ctx.getUnderlineY() == element.getPosition().getY()) {
                        // The elements are on the same line
                        // -> use same descent and line width as before and draw line under white space as well
                        descent = ctx.getUnderlineDescent();
                        lineWidth = ctx.getUnderlineLineWidth();
                        lineStartX = ctx.getUnderlineX();
                    }
                }

                ctx.setCurrentlyUnderlined(true);
                ctx.setUnderlineDescent(descent);
                ctx.setUnderlineLineWidth(lineWidth);
                ctx.setUnderlineY(element.getPosition().getY());
                ctx.setUnderlineX(lineEndX);

                double lineY = y + descent;

                out.setLineWidth((float) lineWidth);
                out.moveTo((float) lineStartX, (float) lineY);
                out.lineTo((float) lineEndX, (float) lineY);
                out.stroke();

                return;
            }
        }

        ctx.setCurrentlyUnderlined(false);
    }

}
