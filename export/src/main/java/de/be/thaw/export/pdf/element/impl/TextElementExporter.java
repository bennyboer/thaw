package de.be.thaw.export.pdf.element.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.export.pdf.util.ElementLocator;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.font.util.KernedSize;
import de.be.thaw.reference.Reference;
import de.be.thaw.reference.ReferenceType;
import de.be.thaw.reference.impl.ExternalReference;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.ReferenceStyle;
import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;
import de.be.thaw.typeset.page.impl.PageNumberPlaceholderElement;
import de.be.thaw.typeset.page.impl.TextElement;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

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

        DocumentNode node = te.getNode().orElseThrow();

        // Load reference (if any) from the original document
        Reference reference = ctx.getDocument().getReferenceModel().getReference(node.getId()).orElse(null);

        PDPageContentStream out = ctx.getContentStream();

        try {
            out.beginText();

            ThawPdfFont font = (ThawPdfFont) ctx.getFontForNode(node);
            double fontSize = ctx.getFontSizeForNode(node);

            // Apply font
            out.setFont(font.getPdFont(), (float) fontSize);

            // Apply font color
            ColorStyle colorStyle = getFontColor(node, reference);
            out.setNonStrokingColor(new PDColor(
                    new float[]{colorStyle.getRed().floatValue(), colorStyle.getGreen().floatValue(), colorStyle.getBlue().floatValue()},
                    PDDeviceRGB.INSTANCE
            ));

            double[] kerningAdjustments = te.getMetrics().getKerningAdjustments();
            double baseline = font.getAscent(fontSize);

            // Check if the element is really just a placeholder for the current page number
            if (te instanceof PageNumberPlaceholderElement) {
                // We need to determine the page number of the target here to display as text
                InternalReference internalReference = (InternalReference) reference;
                ElementLocator targetLocator = ctx.getElementLookup().get(internalReference.getTargetID());
                int pageNumber = targetLocator.getPageNumber();
                String pageNumberStr = String.valueOf(pageNumber);

                KernedSize size;
                try {
                    size = font.getKernedStringSize(-1, pageNumberStr, fontSize);
                } catch (Exception e) {
                    throw new ExportException(e);
                }

                te.setPosition(new Position(te.getPosition().getX() - size.getWidth(), te.getPosition().getY()));
                te.setSize(new Size(size.getWidth(), size.getHeight()));
                te.setText(pageNumberStr);
                kerningAdjustments = size.getKerningAdjustments();
            }

            double y = ctx.getCurrentPage().getMediaBox().getUpperRightY() - element.getPosition().getY() - baseline;
            double x = te.getPosition().getX();

            // Set the position of where to draw the text
            out.newLineAtOffset((float) x, (float) y);

            // Show the text
            if (kerningAdjustments != null) {
                showTextWithKerning(out, font, te.getText(), fontSize, kerningAdjustments);
            } else {
                out.showText(te.getText());
            }

            out.endText();

            underlineIfNecessary(te, ctx, out, font.getPdFont(), fontSize, y);
        } catch (IOException e) {
            throw new ExportException("Text element could not be exported to PDF due to another exception", e);
        }
    }

    @Override
    public void afterExport(Element element, ExportContext ctx) throws ExportException {
        TextElement te = (TextElement) element;

        DocumentNode node = te.getNode().orElseThrow();

        // Load reference (if any) from the original document
        Reference reference = ctx.getDocument().getReferenceModel().getReference(node.getId()).orElse(null);

        createLinkIfNecessary(te, ctx, reference);
    }

    /**
     * Get the font color for the passed node and reference (if any).
     *
     * @param node      to get font color for
     * @param reference if there is one
     * @return font color
     */
    private ColorStyle getFontColor(DocumentNode node, Reference reference) {
        if (reference != null) {
            if (reference.getType() == ReferenceType.INTERNAL) {
                return node.getStyle().getStyleAttribute(
                        StyleType.REFERENCE,
                        style -> Optional.ofNullable(((ReferenceStyle) style).getInternalColor())
                ).orElseThrow();
            } else {
                return node.getStyle().getStyleAttribute(
                        StyleType.REFERENCE,
                        style -> Optional.ofNullable(((ReferenceStyle) style).getExternalColor())
                ).orElseThrow();
            }
        } else {
            return node.getStyle().getStyleAttribute(
                    StyleType.FONT,
                    style -> Optional.ofNullable(((FontStyle) style).getColor())
            ).orElseThrow();
        }
    }

    /**
     * Show the passed text for the given font with kerning adjustments applied.
     *
     * @param out                to write text to
     * @param font               to use
     * @param text               to write
     * @param fontSize           to display text with
     * @param kerningAdjustments of the text
     */
    private void showTextWithKerning(PDPageContentStream out, ThawPdfFont font, String text, double fontSize, double[] kerningAdjustments) throws IOException {
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
        if (element.getNode().orElseThrow().getTextNode().getType() == NodeType.FORMATTED) {
            FormattedNode fn = (FormattedNode) element.getNode().orElseThrow().getTextNode();

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

                out.setStrokingColor(0.0f, 0.0f, 0.0f); // TODO Set to same color as text
                out.setLineWidth((float) lineWidth);
                out.moveTo((float) lineStartX, (float) lineY);
                out.lineTo((float) lineEndX, (float) lineY);
                out.stroke();

                return;
            }
        }

        ctx.setCurrentlyUnderlined(false);
    }

    /**
     * Create a link in the PDF if necessary
     *
     * @param element   the text element
     * @param ctx       the export context
     * @param reference to create link for
     */
    private void createLinkIfNecessary(TextElement element, ExportContext ctx, Reference reference) throws ExportException {
        if (reference == null) {
            return; // No link needed
        }

        double y = ctx.getCurrentPage().getMediaBox().getUpperRightY() - element.getPosition().getY() - element.getMetrics().getBaseline();

        PDRectangle rect = new PDRectangle(
                (float) element.getPosition().getX(),
                (float) y,
                (float) element.getSize().getWidth(),
                (float) element.getSize().getHeight()
        );

        PDBorderStyleDictionary borderStyle = new PDBorderStyleDictionary();
        borderStyle.setStyle(PDBorderStyleDictionary.STYLE_SOLID);
        borderStyle.setWidth(0.0f); // Hide border!

        PDAnnotationLink link = new PDAnnotationLink();
        link.setRectangle(rect);
        link.setBorderStyle(borderStyle);

        if (reference.getType() == ReferenceType.INTERNAL) {
            InternalReference internalReference = (InternalReference) reference;

            ElementLocator targetLocator = ctx.getElementLookup().get(internalReference.getTargetID());

            PDPageXYZDestination destination = new PDPageXYZDestination();
            destination.setPage(ctx.getPdDocument().getPage(targetLocator.getPageNumber() - 1));

            double otherY = ctx.getCurrentPage().getMediaBox().getUpperRightY() - targetLocator.getElement().getPosition().getY();
            destination.setTop((int) otherY);

            PDActionGoTo action = new PDActionGoTo();
            action.setDestination(destination);

            link.setAction(action);
        } else if (reference.getType() == ReferenceType.EXTERNAL) {
            ExternalReference externalReference = (ExternalReference) reference;

            PDActionURI action = new PDActionURI();
            action.setURI(externalReference.getTargetUrl());

            link.setAction(action);
        }

        try {
            ctx.getPdDocument().getPage(element.getPageNumber() - 1).getAnnotations().add(link);
        } catch (IOException e) {
            throw new ExportException("Could not create a link properly", e);
        }
    }

}
