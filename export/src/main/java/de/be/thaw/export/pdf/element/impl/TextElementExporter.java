package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.ExportContext;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.TextElement;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
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

            PDFont font = ctx.getFontForNode(te.getNode());
            double fontSize = ctx.getFontSizeForNode(te.getNode());

            double y = ctx.getCurrentPage().getMediaBox().getUpperRightY() - te.getPosition().getY();

            out.setFont(font, (float) fontSize);
            out.newLineAtOffset((float) te.getPosition().getX(), (float) y);

            out.showText(te.getText());

            out.endText();

            underlineIfNecessary(te, ctx, out, font, fontSize, y);
        } catch (IOException e) {
            throw new ExportException("Text element could not be exported to PDF due to another exception", e);
        }
    }

    private void underlineIfNecessary(TextElement element, ExportContext ctx, PDPageContentStream out, PDFont font, double fontSize, double y) throws IOException {
        if (element.getNode().getType() == NodeType.FORMATTED) {
            FormattedNode fn = (FormattedNode) element.getNode();

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
