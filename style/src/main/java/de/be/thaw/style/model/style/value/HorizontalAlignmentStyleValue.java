package de.be.thaw.style.model.style.value;

import de.be.thaw.util.HorizontalAlignment;

/**
 * Style value of horizontal alignment.
 */
public class HorizontalAlignmentStyleValue extends AbstractStyleValue {

    /**
     * The horizontal alignment.
     */
    private final HorizontalAlignment alignment;

    public HorizontalAlignmentStyleValue(HorizontalAlignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public String value() {
        return alignment.name().toLowerCase();
    }

    @Override
    public HorizontalAlignment horizontalAlignment() {
        return alignment;
    }

}
