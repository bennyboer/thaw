package de.be.thaw.font.util;

import java.util.Map;

/**
 * Utility class providing access to superscript characters for numbers for example.
 */
public class SuperScriptUtil {

    /**
     * Mapping of characters to their corresponding super script characters.
     */
    private static final Map<Character, Character> SUPER_SCRIPT_CHARACTERS = Map.ofEntries(
            Map.entry('0', '\u2070'),
            Map.entry('1', '\u00B9'),
            Map.entry('2', '\u00B2'),
            Map.entry('3', '\u00B3'),
            Map.entry('4', '\u2074'),
            Map.entry('5', '\u2075'),
            Map.entry('6', '\u2076'),
            Map.entry('7', '\u2077'),
            Map.entry('8', '\u2078'),
            Map.entry('9', '\u2079')
    );

    /**
     * Get a super script character for the passed number.
     *
     * @param number to get character for (must be in range 0 to 9).
     * @return the super script character for the passed number
     */
    public static char getSuperScriptCharForNumber(int number) {
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("Provided number must be in range 0 to 9");
        }

        return SUPER_SCRIPT_CHARACTERS.get(String.valueOf(number).charAt(0));
    }

    /**
     * Get the passed number converted to a string of super script number characters.
     *
     * @param number to convert
     * @return super script character string for the passed number
     */
    public static String getSuperScriptCharsForNumber(int number) {
        String numStr = String.valueOf(number);

        StringBuilder sb = new StringBuilder();
        numStr.chars().forEach(c -> sb.append(SUPER_SCRIPT_CHARACTERS.get((char) c)));

        return sb.toString();
    }

}
