package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.text.TextAlignment;

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
    private final TextAlignment alignment;

    /**
     * Whether to justify the text.
     */
    private final Boolean justify;

    public TextStyle(
            Double firstLineIndent,
            Double lineHeight,
            TextAlignment alignment,
            Boolean justify
    ) {
        this.firstLineIndent = firstLineIndent;
        this.lineHeight = lineHeight;
        this.alignment = alignment;
        this.justify = justify;
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
                justify != null ? justify : other.getJustify()
        );
    }

    public Double getFirstLineIndent() {
        return firstLineIndent;
    }

    public Double getLineHeight() {
        return lineHeight;
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    public Boolean getJustify() {
        return justify;
    }

}
