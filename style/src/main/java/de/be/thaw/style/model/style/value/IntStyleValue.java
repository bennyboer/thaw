package de.be.thaw.style.model.style.value;

import de.be.thaw.util.unit.Unit;
import org.jetbrains.annotations.Nullable;

/**
 * Value of type integer.
 */
public class IntStyleValue extends AbstractStyleValue {

    /**
     * The integer value.
     */
    private final int value;

    /**
     * Unit of the value.
     */
    private final Unit unit;

    public IntStyleValue(int value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public String value() {
        return String.format("%d%s", value, unit.getShortName());
    }

    @Override
    public int intValue(@Nullable Unit targetUnit) {
        return (int) doubleValue(targetUnit);
    }

    @Override
    public double doubleValue(@Nullable Unit targetUnit) {
        if (targetUnit == null) {
            return value; // Having not enough information to do a conversion
        }

        return Unit.convert(value, unit(), targetUnit);
    }

    @Override
    public boolean booleanValue() {
        return value != 0;
    }

    @Override
    public Unit unit() {
        return unit;
    }

}
