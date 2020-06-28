package de.be.thaw.export.pdf;

import de.be.thaw.core.document.Document;
import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Exporter exporting documents to PDF.
 */
public class PdfExporter implements Exporter {

    @Override
    public void export(Document document, Path path) throws ExportException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            PDFont pdfFont = PDType1Font.HELVETICA;
            float fontSize = 16;
            float leading = 1.5f * fontSize;

            PDRectangle box = page.getMediaBox();
            float margin = 72;
            float width = box.getWidth() - 2 * margin;
            float height = box.getHeight() - 2 * margin;
            float startX = box.getLowerLeftX() + margin;
            float startY = box.getUpperRightY() - margin;
            float endX = box.getUpperRightX() - margin;
            float endY = box.getLowerLeftY() + margin;

            TextModel textModel = document.getTextModel();
            List<List<Node>> verticalList = toVerticalList(textModel);

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            ExportContext ctx = new ExportContext(contentStream, pdfFont, fontSize, width, height, margin, margin, margin, margin, startY, startX, endY, endX, leading);

            contentStream.beginText();
            contentStream.setFont(pdfFont, fontSize);
            contentStream.newLineAtOffset(startX, startY);

            for (List<Node> horizontalList : verticalList) {
                exportHorizontalList(horizontalList, ctx);
            }

            contentStream.endText();
            contentStream.close();

            doc.save(path.toFile());
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    /**
     * Export the passed horizontal list.
     *
     * @param horizontalList to export
     * @param ctx            used during exporting
     * @throws ExportException in case something went wrong
     */
    private void exportHorizontalList(List<Node> horizontalList, ExportContext ctx) throws ExportException {
        System.out.println("--- NEXT HORIZONTAL LIST ---");
        for (Node node : horizontalList) {
            switch (node.getType()) {
                case TEXT -> exportTextNode((TextNode) node, ctx);
                case FORMATTED -> exportFormattedNode((FormattedNode) node, ctx);
                default -> throw new ExportException("Node cannot be exported as it does not carry a value");
            }
        }
    }

    /**
     * Export the passed text node.
     *
     * @param node to export
     * @param ctx  used during exporting
     * @throws ExportException in case something went wrong
     */
    private void exportTextNode(TextNode node, ExportContext ctx) throws ExportException {
        try {
            String text = node.getValue();

            int whiteSpaceIdx = -1;
            int startIdx = 0;
            while (true) {
                whiteSpaceIdx = text.indexOf(' ', whiteSpaceIdx + 1);
                if (whiteSpaceIdx == -1) {
                    // Print rest of the string
                    String rest = text.substring(startIdx);
                    float size = ctx.getFont().getStringWidth(rest) / 1000 * ctx.getFontSize();

                    if (size + ctx.getOffsetLeft() > ctx.getEndRight()) {
                        // Next line
                        ctx.setOffsetLeft(ctx.getStartLeft());
                        ctx.setOffsetTop(ctx.getOffsetTop() + ctx.getLeading());
                        ctx.getContentStream().newLineAtOffset(0, -ctx.getLeading());
                    }

                    ctx.getContentStream().showText(rest);

                    ctx.setOffsetLeft(ctx.getOffsetLeft() + size);

                    break; // No more white spaces in the text
                }

                String part = text.substring(startIdx, whiteSpaceIdx + 1);
                startIdx = whiteSpaceIdx + 1;

                float size = ctx.getFont().getStringWidth(part) / 1000 * ctx.getFontSize();

                if (size + ctx.getOffsetLeft() > ctx.getEndRight()) {
                    // Next line
                    ctx.setOffsetLeft(ctx.getStartLeft());
                    ctx.setOffsetTop(ctx.getOffsetTop() + ctx.getLeading());
                    ctx.getContentStream().newLineAtOffset(0, -ctx.getLeading());
                }

                ctx.getContentStream().showText(part);

                ctx.setOffsetLeft(ctx.getOffsetLeft() + size);
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    /**
     * Export the passed formatted node.
     *
     * @param node to export
     * @param ctx  used during exporting
     * @throws ExportException in case something went wrong
     */
    private void exportFormattedNode(FormattedNode node, ExportContext ctx) throws ExportException {
        System.out.print(node.getValue());
    }

    /**
     * Convert the passed text model into a vertical list.
     *
     * @param textModel to convert
     * @return vertical list
     * @throws ExportException in case something went wrong
     */
    private List<List<Node>> toVerticalList(TextModel textModel) throws ExportException {
        List<List<Node>> verticalList = new ArrayList<>();

        Node root = textModel.getRoot();
        for (Node box : root.children()) {
            fillVerticalList(box, verticalList);
        }

        return verticalList;
    }

    /**
     * Find horizontal lists in the passed box node and add them to the given vertical list.
     *
     * @param node         to find lists in
     * @param verticalList to add found horizontal lists to
     * @throws ExportException in case something went wrong
     */
    private void fillVerticalList(Node node, List<List<Node>> verticalList) throws ExportException {
        if (verticalList.isEmpty()) {
            verticalList.add(new ArrayList<>()); // Add the initial horizontal list
        }

        List<Node> horizontalList = verticalList.get(verticalList.size() - 1);

        switch (node.getType()) {
            case TEXT -> {
                horizontalList.add(node);
            }
            case FORMATTED -> {
                horizontalList.add(node);

                if (node.hasChildren()) {
                    for (Node child : node.children()) {
                        fillVerticalList(child, verticalList);
                    }
                }
            }
            case ENUMERATION_ITEM -> {
                if (!horizontalList.isEmpty()) {
                    horizontalList = new ArrayList<>();
                    verticalList.add(horizontalList);
                }

                if (node.hasChildren()) {
                    for (Node child : node.children()) {
                        fillVerticalList(child, verticalList);
                    }
                }
            }
            default -> {
                if (node.hasChildren()) {
                    for (Node child : node.children()) {
                        fillVerticalList(child, verticalList);
                    }
                } else {
                    throw new ExportException(String.format("Cannot export node type '%s'", node.getType()));
                }
            }
        }
    }

}
