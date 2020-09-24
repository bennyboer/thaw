package de.be.thaw.style.model.style.util.list.generator;

import de.be.thaw.style.model.style.util.list.ListStyleGenerator;

import java.util.TreeMap;

/**
 * Generator for roman list style items.
 */
public class RomanSequenceGenerator implements ListStyleGenerator {

    /**
     * Collection of roman letters.
     */
    private static final TreeMap<Integer, String> ROMAN_LETTERS = new TreeMap<>();

    static {
        ROMAN_LETTERS.put(1000, "M");
        ROMAN_LETTERS.put(900, "CM");
        ROMAN_LETTERS.put(500, "D");
        ROMAN_LETTERS.put(400, "CD");
        ROMAN_LETTERS.put(100, "C");
        ROMAN_LETTERS.put(90, "XC");
        ROMAN_LETTERS.put(50, "L");
        ROMAN_LETTERS.put(40, "XL");
        ROMAN_LETTERS.put(10, "X");
        ROMAN_LETTERS.put(9, "IX");
        ROMAN_LETTERS.put(5, "V");
        ROMAN_LETTERS.put(4, "IV");
        ROMAN_LETTERS.put(1, "I");
    }

    /**
     * Whether to generate upper-case items.
     */
    private final boolean isUpperCase;

    public RomanSequenceGenerator(boolean isUpperCase) {
        this.isUpperCase = isUpperCase;
    }

    @Override
    public String generate(int sequence) {
        String result = generateRomanNumberString(sequence);

        if (!isUpperCase) {
            result = result.toLowerCase();
        }

        return result;
    }

    /**
     * Generate a roman number string for the given number.
     *
     * @param number to generate roman number string for
     * @return roman number string
     */
    private String generateRomanNumberString(int number) {
        final int bestKey = ROMAN_LETTERS.floorKey(number);
        if (number == bestKey) {
            return ROMAN_LETTERS.get(number);
        }

        return ROMAN_LETTERS.get(bestKey) + generate(number - bestKey);
    }

}
