package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * Style specifying an indent in the first line of a paragraph.
 */
public class FirstLineIndentStyle implements Style {

    /**
     * The indentation value.
     */
    private final double indent;

    public FirstLineIndentStyle(double indent) {
        this.indent = indent;
    }

    @Override
    public StyleType getType() {
        return StyleType.FIRST_LINE_INDENT;
    }

    public double getIndent() {
        return indent;
    }

}
