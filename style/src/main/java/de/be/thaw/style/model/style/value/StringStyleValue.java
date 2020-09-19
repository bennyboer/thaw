package de.be.thaw.style.model.style.value;

/**
 * Style value of a string.
 */
public class StringStyleValue extends AbstractStyleValue {

    /**
     * The value.
     */
    private final String value;

    public StringStyleValue(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

}
