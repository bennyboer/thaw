package de.be.thaw.export.pdf;

import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.typeset.page.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Exporter exporting documents to PDF.
 */
public class PdfExporter implements Exporter {

    @Override
    public void export(List<Page> pages, Path path) throws ExportException {
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

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);
            ExportContext ctx = new ExportContext(contentStream, pdfFont, fontSize, width, height, margin, margin, margin, margin, startY, startX, endY, endX, leading);

            contentStream.beginText();
            contentStream.setFont(pdfFont, fontSize);
            contentStream.newLineAtOffset(startX, startY);

            // TODO

            contentStream.endText();
            contentStream.close();

            doc.save(path.toFile());
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

}
