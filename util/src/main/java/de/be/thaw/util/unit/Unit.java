package de.be.thaw.util.unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Enumeration of supported units.
 */
public enum Unit {

    /**
     * Our base unit all other calculation factors should align to.
     */
    MILLIMETER("millimeter", "mm", 1),
    CENTIMETER("centimeter", "cm", 10),
    INCH("inch", "in", 25.4),
    POINTS("points", "pt", 25.4 / 72),
    PIXEL("pixel", "px", 0.75 * 25.4 / 72);

    /**
     * Lookup for units by their short name.
     */
    private static final Map<String, Unit> SHORT_NAME_LOOKUP = new HashMap<>();

    static {
        for (Unit unit : Unit.values()) {
            SHORT_NAME_LOOKUP.put(unit.getShortName().toLowerCase(), unit);
        }
    }

    /**
     * Name of the unit.
     */
    private final String name;

    /**
     * Short name of the unit.
     */
    private final String shortName;

    /**
     * Factor used to convert a value of the current unit to millimeters.
     * So CURRENT_VALUE * mmConversionFactor should give the current value in millimeters!
     */
    private final double mmConversionFactor;

    Unit(String name, String shortName, double mmConversionFactor) {
        this.name = name;
        this.shortName = shortName;
        this.mmConversionFactor = mmConversionFactor;
    }

    /**
     * Get the name of the unit.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the short name of the unit.
     *
     * @return short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Get the conversion factor to millimeters.
     * So the current value in a unit should be calculated to millimeters using:
     * CURRENT_VALUE * mmConversionFactor.
     *
     * @return millimeter conversion factor
     */
    public double getMmConversionFactor() {
        return mmConversionFactor;
    }

    /**
     * Convert the passed value in the current unit to the given target unit.
     *
     * @param value      in the given valueUnit to convert
     * @param valueUnit  unit of the passed value
     * @param targetUnit the target unit after the conversion
     * @return the passed value converted in the target unit
     */
    public static double convert(double value, Unit valueUnit, Unit targetUnit) {
        // Calculate Millimeters (base unit) for the given value.
        double baseValue = value * valueUnit.mmConversionFactor;

        // Calculate target unit value for base value (Millimeters).
        return baseValue / targetUnit.mmConversionFactor;
    }

    /**
     * Get a unit for the passed short name.
     *
     * @param shortName to get unit for
     * @return unit or empty Optional
     */
    public static Optional<Unit> forShortName(String shortName) {
        return Optional.ofNullable(SHORT_NAME_LOOKUP.get(shortName.toLowerCase()));
    }

}
