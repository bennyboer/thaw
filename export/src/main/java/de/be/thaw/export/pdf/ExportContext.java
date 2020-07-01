package de.be.thaw.export.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Context used during PDF export.
 */
public class ExportContext {

    /**
     * Current offset from left.
     */
    private float offsetLeft = 0;

    /**
     * Current offset from top.
     */
    private float offsetTop = 0;

    /**
     * Content stream used to write text to the PDF.
     */
    private final PDPageContentStream contentStream;

    /**
     * The current font to use.
     */
    private PDFont font;

    /**
     * Size of the font.
     */
    private float fontSize;

    /**
     * The maximum de.be.thaw.typeset.page width.
     */
    private final float pageWidth;

    /**
     * The maximum de.be.thaw.typeset.page height;
     */
    private final float pageHeight;

    /**
     * Left margin.
     */
    private final float marginLeft;

    /**
     * Top margin.
     */
    private final float marginTop;

    /**
     * Right margin.
     */
    private final float marginRight;

    /**
     * Bottom margin.
     */
    private final float marginBottom;

    private final float startTop;

    private final float startLeft;

    private final float endBottom;

    private final float endRight;

    private final float leading;

    public ExportContext(
            PDPageContentStream contentStream,
            PDFont font,
            float fontSize,
            float pageWidth,
            float pageHeight,
            float marginTop,
            float marginLeft,
            float marginBottom,
            float marginRight,
            float startTop,
            float startLeft,
            float endBottom,
            float endRight,
            float leading
    ) {
        this.contentStream = contentStream;
        this.font = font;
        this.fontSize = fontSize;

        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;

        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;

        this.startTop = startTop;
        this.startLeft = startLeft;
        this.endBottom = endBottom;
        this.endRight = endRight;

        this.leading = leading;
    }

    public float getOffsetLeft() {
        return offsetLeft;
    }

    public void setOffsetLeft(float offsetLeft) {
        this.offsetLeft = offsetLeft;
    }

    public float getOffsetTop() {
        return offsetTop;
    }

    public void setOffsetTop(float offsetTop) {
        this.offsetTop = offsetTop;
    }

    public PDPageContentStream getContentStream() {
        return contentStream;
    }

    public PDFont getFont() {
        return font;
    }

    public void setFont(PDFont font) {
        this.font = font;
    }

    public float getPageWidth() {
        return pageWidth;
    }

    public float getPageHeight() {
        return pageHeight;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public float getStartTop() {
        return startTop;
    }

    public float getStartLeft() {
        return startLeft;
    }

    public float getEndBottom() {
        return endBottom;
    }

    public float getEndRight() {
        return endRight;
    }

    public float getLeading() {
        return leading;
    }

    public float getFontSize() {
        return fontSize;
    }

}
