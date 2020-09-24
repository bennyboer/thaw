package de.be.thaw.style.model.style.value;

import de.be.thaw.util.unit.Unit;
import org.jetbrains.annotations.Nullable;

/**
 * Style value of a boolean
 */
public class BooleanStyleValue extends AbstractStyleValue {

    /**
     * The value.
     */
    private final boolean value;

    public BooleanStyleValue(boolean value) {
        this.value = value;
    }

    @Override
    public String value() {
        return String.valueOf(value);
    }

    @Override
    public boolean booleanValue() {
        return value;
    }

    @Override
    public int intValue(@Nullable Unit defaultUnit, @Nullable Unit targetUnit) {
        return value ? 1 : 0;
    }

    @Override
    public double doubleValue(@Nullable Unit defaultUnit, @Nullable Unit targetUnit) {
        return value ? 1.0 : 0.0;
    }

}
