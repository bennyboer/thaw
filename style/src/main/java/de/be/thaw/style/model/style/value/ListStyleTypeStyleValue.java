package de.be.thaw.style.model.style.value;

import de.be.thaw.style.model.style.util.list.ListStyleType;

/**
 * Value of type list style type.
 */
public class ListStyleTypeStyleValue extends AbstractStyleValue {

    /**
     * The list style type.
     */
    private final ListStyleType type;

    public ListStyleTypeStyleValue(ListStyleType type) {
        this.type = type;
    }

    @Override
    public String value() {
        return type.name().toLowerCase();
    }

    @Override
    public ListStyleType listStyleType() {
        return type;
    }

}
