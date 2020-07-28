package de.be.thaw.math.mathml.typeset.util;

import de.be.thaw.math.mathml.tree.node.MathVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods dealing with math variants.
 */
public class MathVariantUtil {

    /**
     * The latin alphabet.
     */
    private static final String LATIN_ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * The latin digits.
     */
    private static final String LATIN_DIGITS = "0123456789";

    /**
     * The greek alphabet with capital letters.
     */
    private static final String GREEK_ALPHABET_CAPITAL_LETTERS = "\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398\u0399\u039A\u039B\u039C\u039D\u039E\u039F\u03A0\u03A1\u03A2\u03A3\u03A4\u03A5\u03A6\u03A7\u03A8\u03A9\u03AA";

    /**
     * The greek alphabet with small letters.
     */
    private static final String GREEK_ALPHABET_SMALL_LETTERS = "\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6\u03B7\u03B8\u03B9\u03BA\u03BB\u03BC\u03BD\u03BE\u03BF\u03C0\u03C1\u03C2\u03C3\u03C4\u03C5\u03C6\u03C7\u03C8\u03C9\u03CA\u03CB\u03CC\u03CD\u03CE\u03CF\u03D0";

    /**
     * Supported small latin letters offset lookup.
     */
    private static final Map<Integer, Integer> SMALL_LATIN_LETTERS_OFFSET = new HashMap<>();

    /**
     * Supported capital latin letters offset lookup.
     */
    private static final Map<Integer, Integer> CAPITAL_LATIN_LETTERS_OFFSET = new HashMap<>();

    /**
     * Supported small greek letters offset lookup.
     */
    private static final Map<Integer, Integer> SMALL_GREEK_LETTERS_OFFSET = new HashMap<>();

    /**
     * Supported capital greek letters offset lookup.
     */
    private static final Map<Integer, Integer> CAPITAL_GREEK_LETTERS_OFFSET = new HashMap<>();

    /**
     * Supported digits offset lookup
     */
    private static final Map<Integer, Integer> LATIN_DIGITS_OFFSET = new HashMap<>();

    /**
     * Set of arithmetic operators.
     */
    private static final Set<Character> ARITHMETIC_OPERATORS = Set.of('+', '-', '*', '/');

    static {
        int offset = 0;
        for (char c : LATIN_ALPHABET.toCharArray()) {
            SMALL_LATIN_LETTERS_OFFSET.put((int) c, offset);
            offset++;
        }

        offset = 0;
        for (char c : LATIN_ALPHABET.toUpperCase().toCharArray()) {
            CAPITAL_LATIN_LETTERS_OFFSET.put((int) c, offset);
            offset++;
        }

        offset = 0;
        for (char c : GREEK_ALPHABET_SMALL_LETTERS.toCharArray()) {
            SMALL_GREEK_LETTERS_OFFSET.put((int) c, offset);
            offset++;
        }

        offset = 0;
        for (char c : GREEK_ALPHABET_CAPITAL_LETTERS.toCharArray()) {
            CAPITAL_GREEK_LETTERS_OFFSET.put((int) c, offset);
            offset++;
        }

        offset = 0;
        for (char c : LATIN_DIGITS.toCharArray()) {
            LATIN_DIGITS_OFFSET.put((int) c, offset);
            offset++;
        }
    }

    /**
     * Convert the passed string using the passed math variant.
     *
     * @param string  to convert
     * @param variant the math variant to use
     * @return the converted string
     */
    public static String convertStringUsingMathVariant(String string, MathVariant variant) {
        StringBuilder builder = new StringBuilder();

        int len = string.length();
        for (int i = 0; i < len; ) {
            int codePoint = string.codePointAt(i);
            i += Character.charCount(codePoint);

            if (Character.isDigit(codePoint)) {
                builder.append(variant.getDigitLetter(LATIN_DIGITS_OFFSET.get(codePoint)));
                continue;
            }

            if (CAPITAL_LATIN_LETTERS_OFFSET.containsKey(codePoint)) {
                builder.append(variant.getCapitalLetter(CAPITAL_LATIN_LETTERS_OFFSET.get(codePoint)));
                continue;
            }

            if (SMALL_LATIN_LETTERS_OFFSET.containsKey(codePoint)) {
                builder.append(variant.getSmallLetter(SMALL_LATIN_LETTERS_OFFSET.get(codePoint)));
                continue;
            }

            if (CAPITAL_GREEK_LETTERS_OFFSET.containsKey(codePoint)) {
                builder.append(variant.getGreekCapitalLetter(CAPITAL_GREEK_LETTERS_OFFSET.get(codePoint)));
                continue;
            }

            if (SMALL_GREEK_LETTERS_OFFSET.containsKey(codePoint)) {
                builder.append(variant.getGreekSmallLetter(SMALL_GREEK_LETTERS_OFFSET.get(codePoint)));
                continue;
            }

            builder.append(Character.toChars(codePoint));
        }

        return builder.toString();
    }

    /**
     * Check if the passed character is an arithmetic operator.
     *
     * @param c to check
     * @return whether c represents an arithmetic operator
     */
    public static boolean isArithmeticOperator(char c) {
        return ARITHMETIC_OPERATORS.contains(c);
    }

}
