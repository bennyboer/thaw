package de.be.thaw.style.model.style.value;

import de.be.thaw.font.util.KerningMode;

/**
 * A kerning style value.
 */
public class KerningModeStyleValue extends AbstractStyleValue {

    /**
     * The kerning mode.
     */
    private final KerningMode mode;

    public KerningModeStyleValue(KerningMode mode) {
        this.mode = mode;
    }

    @Override
    public String value() {
        return mode.name().toLowerCase();
    }

    @Override
    public KerningMode kerningMode() {
        return mode;
    }

}
