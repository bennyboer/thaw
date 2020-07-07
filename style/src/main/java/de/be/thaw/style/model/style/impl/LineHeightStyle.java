package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * Style specifying the line height of a paragraph.
 */
public class LineHeightStyle implements Style {

    /**
     * The line height.
     */
    private final double lineHeight;

    public LineHeightStyle(double lineHeight) {
        this.lineHeight = lineHeight;
    }

    @Override
    public StyleType getType() {
        return StyleType.FIRST_LINE_INDENT;
    }

    public double getLineHeight() {
        return lineHeight;
    }

}
