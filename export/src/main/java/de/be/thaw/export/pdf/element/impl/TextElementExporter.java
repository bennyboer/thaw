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

            if (te.getNode().getType() == NodeType.FORMATTED) {
                FormattedNode fn = (FormattedNode) te.getNode();

                if (fn.getEmphases().contains(TextEmphasis.UNDERLINED)) {
                    double descent = font.getFontDescriptor().getDescent() / 1000 * fontSize;
                    double lineY = y + descent;

                    out.setLineWidth(font.getFontDescriptor().getFontWeight() / 1000);
                    out.moveTo((float) te.getPosition().getX(), (float) lineY);
                    out.lineTo((float) (te.getPosition().getX() + te.getSize().getWidth()), (float) lineY);
                    out.stroke();
                }
            }
        } catch (IOException e) {
            throw new ExportException("Text element could not be exported to PDF due to another exception", e);
        }
    }

}
