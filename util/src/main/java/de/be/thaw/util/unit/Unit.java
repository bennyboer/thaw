package de.be.thaw.util.unit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Enumeration of supported units.
 */
public enum Unit {

    MILLIMETER("millimeter", "mm", BaseUnit.MILLIMETER, 1),
    CENTIMETER("centimeter", "cm", BaseUnit.MILLIMETER, 10),
    INCH("inch", "in", BaseUnit.MILLIMETER, 25.4),
    POINTS("points", "pt", BaseUnit.MILLIMETER, 25.4 / 72),
    PIXEL("pixel", "px", BaseUnit.MILLIMETER, 0.75 * 25.4 / 72),

    UNITARY("unitary", "", BaseUnit.UNITARY, 1),
    PERCENT("percent", "%", BaseUnit.UNITARY, 0.01);

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
     * The base unit this unit is able to be converted to.
     */
    private final BaseUnit baseUnit;

    /**
     * Factor used to convert a value of the current unit to the base unit.
     * So CURRENT_VALUE * baseUnitConversionFactor should give the current value in the base unit!
     */
    private final double baseUnitConversionFactor;

    Unit(String name, String shortName, BaseUnit baseUnit, double baseUnitConversionFactor) {
        this.name = name;
        this.shortName = shortName;

        this.baseUnit = baseUnit;
        this.baseUnitConversionFactor = baseUnitConversionFactor;
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
     * Get the base unit this unit can be converted to.
     *
     * @return base unit
     */
    public BaseUnit getBaseUnit() {
        return baseUnit;
    }

    /**
     * Get the conversion factor to the base unit.
     * So the current value in a unit should be calculated to the base unit using:
     * CURRENT_VALUE * baseUnitConversionFactor.
     *
     * @return base unit conversion factor
     */
    public double getBaseUnitConversionFactor() {
        return baseUnitConversionFactor;
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
        // Check whether the two units share the same base unit, otherwise we cannot convert them
        if (valueUnit.getBaseUnit() != targetUnit.getBaseUnit()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot convert a value in the '%s' unit to the '%s' unit as they do not share the same base unit",
                    valueUnit.name,
                    targetUnit.name
            ));
        }

        // Calculate the value in the base unit from the current unit.
        double baseValue = value * valueUnit.baseUnitConversionFactor;

        // Calculate value in the target unit from the base unit value.
        return baseValue / targetUnit.baseUnitConversionFactor;
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
