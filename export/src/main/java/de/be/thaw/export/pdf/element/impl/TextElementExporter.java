package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.ExportContext;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.TextElement;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

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

            double fontSize = ctx.getFontSizeForNode(te.getNode());

            double y = ctx.getCurrentPage().getMediaBox().getUpperRightY() - te.getPosition().getY();

            out.setFont(ctx.getFontForNode(te.getNode()), (float) fontSize);
            out.newLineAtOffset((float) te.getPosition().getX(), (float) y);

            out.showText(te.getText());

            out.endText();
        } catch (IOException e) {
            throw new ExportException("Text element could not be exported to PDF due to another exception", e);
        }
    }

}
