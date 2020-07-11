package de.be.thaw.style.model.style.impl;

import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;

/**
 * Style for references.
 */
public class ReferenceStyle implements Style {

    /**
     * Internal reference link color.
     */
    private final ColorStyle internalColor;

    /**
     * External reference link color.
     */
    private final ColorStyle externalColor;

    public ReferenceStyle(ColorStyle internalColor, ColorStyle externalColor) {
        this.internalColor = internalColor;
        this.externalColor = externalColor;
    }

    @Override
    public StyleType getType() {
        return StyleType.REFERENCE;
    }

    @Override
    public Style merge(Style style) {
        if (style == null) {
            return this;
        }

        ReferenceStyle other = (ReferenceStyle) style;

        return new ReferenceStyle(
                getInternalColor() != null ? (ColorStyle) getInternalColor().merge(other.getInternalColor()) : other.getInternalColor(),
                getExternalColor() != null ? (ColorStyle) getExternalColor().merge(other.getExternalColor()) : other.getExternalColor()
        );
    }

    public ColorStyle getInternalColor() {
        return internalColor;
    }

    public ColorStyle getExternalColor() {
        return externalColor;
    }

}
