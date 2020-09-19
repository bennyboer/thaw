package de.be.thaw.style.model.style.value;

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
    public int intValue() {
        return value ? 1 : 0;
    }

    @Override
    public double doubleValue() {
        return value ? 1.0 : 0.0;
    }

}
