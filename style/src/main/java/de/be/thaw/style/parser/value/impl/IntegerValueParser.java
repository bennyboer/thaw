package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.value.IntStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;
import de.be.thaw.util.unit.Unit;

/**
 * Style value parser for integer values.
 */
public class IntegerValueParser implements StyleValueParser {

    /**
     * Default unit of the value (if there is none specified).
     */
    private final Unit defaultUnit;

    public IntegerValueParser(Unit defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    @Override
    public StyleValue parse(String src) throws StyleValueParseException {
        src = src.trim();

        // Try to extract unit from string
        String value = src;
        String unitShortName = null;
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (!Character.isDigit(c) && c != '.' && c != '-') {
                value = src.substring(0, i);
                unitShortName = src.substring(i);
                break;
            }
        }

        try {
            String finalUnitShortName = unitShortName;
            return new IntStyleValue(Integer.parseInt(value), unitShortName != null ? Unit.forShortName(unitShortName).orElseThrow(() -> new StyleValueParseException(String.format(
                    "Could not determine the unit for '%s'",
                    finalUnitShortName
            ))) : defaultUnit);
        } catch (NumberFormatException e) {
            throw new StyleValueParseException(e);
        }
    }

}
