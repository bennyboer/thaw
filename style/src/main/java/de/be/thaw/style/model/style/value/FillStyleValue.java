package de.be.thaw.style.model.style.value;

import de.be.thaw.style.model.style.util.FillStyle;

/**
 * Style value for table of content fills.
 */
public class FillStyleValue extends AbstractStyleValue {

    /**
     * The fill style.
     */
    private final FillStyle fill;

    public FillStyleValue(FillStyle fill) {
        this.fill = fill;
    }

    @Override
    public String value() {
        return fill.name().toLowerCase();
    }

    @Override
    public FillStyle fillStyle() {
        return fill;
    }

}
