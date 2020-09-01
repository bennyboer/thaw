package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.util.HorizontalAlignment;

/**
 * Style of a text block (paragraph).
 */
public class TextStyle implements Style {

    /**
     * The first line indent.
     */
    private final Double firstLineIndent;

    /**
     * Height of a line.
     */
    private final Double lineHeight;

    /**
     * Alignment of the text.
     */
    private final HorizontalAlignment alignment;

    /**
     * Whether to justify the text.
     */
    private final Boolean justify;

    /**
     * Whether line numbers should be shown.
     */
    private final Boolean showLineNumbers;

    /**
     * Font family used to show line numbers (if showLineNumbers is true).
     */
    private final String lineNumberFontFamily;

    /**
     * Font size of the line numbers (when showLineNumbers is true).
     */
    private final Double lineNumberFontSize;

    /**
     * Color of the line number (when showLineNumbers is true).
     */
    private final ColorStyle lineNumberColor;

    public TextStyle(
            Double firstLineIndent,
            Double lineHeight,
            HorizontalAlignment alignment,
            Boolean justify,
            Boolean showLineNumbers,
            String lineNumberFontFamily,
            Double lineNumberFontSize,
            ColorStyle lineNumberColor
    ) {
        this.firstLineIndent = firstLineIndent;
        this.lineHeight = lineHeight;
        this.alignment = alignment;
        this.justify = justify;

        this.showLineNumbers = showLineNumbers;
        this.lineNumberFontFamily = lineNumberFontFamily;
        this.lineNumberFontSize = lineNumberFontSize;
        this.lineNumberColor = lineNumberColor;
    }

    @Override
    public StyleType getType() {
        return StyleType.TEXT;
    }

    @Override
    public Style merge(Style style) {
        if (style == null) {
            return this;
        }

        TextStyle other = (TextStyle) style;

        return new TextStyle(
                firstLineIndent != null ? firstLineIndent : other.getFirstLineIndent(),
                lineHeight != null ? lineHeight : other.getLineHeight(),
                alignment != null ? alignment : other.getAlignment(),
                justify != null ? justify : other.getJustify(),
                showLineNumbers != null ? showLineNumbers : other.isShowLineNumbers(),
                lineNumberFontFamily != null ? lineNumberFontFamily : other.getLineNumberFontFamily(),
                lineNumberFontSize != null ? lineNumberFontSize : other.getLineNumberFontSize(),
                lineNumberColor != null ? lineNumberColor : other.getLineNumberColor()
        );
    }

    public Double getFirstLineIndent() {
        return firstLineIndent;
    }

    public Double getLineHeight() {
        return lineHeight;
    }

    public HorizontalAlignment getAlignment() {
        return alignment;
    }

    public Boolean getJustify() {
        return justify;
    }

    public Boolean isShowLineNumbers() {
        return showLineNumbers;
    }

    public String getLineNumberFontFamily() {
        return lineNumberFontFamily;
    }

    public Double getLineNumberFontSize() {
        return lineNumberFontSize;
    }

    public ColorStyle getLineNumberColor() {
        return lineNumberColor;
    }

}
