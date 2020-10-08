package de.be.thaw.style.model.style.value;

import de.be.thaw.util.VerticalAlignment;

/**
 * Style value of vertical alignment.
 */
public class VerticalAlignmentStyleValue extends AbstractStyleValue {

    /**
     * The vertical alignment.
     */
    private final VerticalAlignment alignment;

    public VerticalAlignmentStyleValue(VerticalAlignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public String value() {
        return alignment.name().toLowerCase();
    }

    @Override
    public VerticalAlignment verticalAlignment() {
        return alignment;
    }

}
