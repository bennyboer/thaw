package de.be.thaw.math.mathml.tree.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Variants of the math font.
 */
public enum MathVariant {

    NORMAL("normal", new char[]{'\u0041'}, new char[]{'\u0061'}, new char[]{'\u0391'}, new char[]{'\u03B1'}, new char[]{'\u0030'}, Map.ofEntries(
            Map.entry((int) '\u03A2', new char[]{'\u03F4'}),
            Map.entry((int) '\u03AA', new char[]{'\u2207'}),
            Map.entry((int) '\u03CA', new char[]{'\u2202'}),
            Map.entry((int) '\u03CB', new char[]{'\u03F5'}),
            Map.entry((int) '\u03CC', new char[]{'\u03D1'}),
            Map.entry((int) '\u03CD', new char[]{'\u03F0'}),
            Map.entry((int) '\u03CE', new char[]{'\u03D5'}),
            Map.entry((int) '\u03CF', new char[]{'\u03F1'}),
            Map.entry((int) '\u03D0', new char[]{'\u03D6'})
    )),
    BOLD("bold", new char[]{'\uD835', '\uDC00'}, new char[]{'\uD835', '\uDC1A'}, new char[]{'\uD835', '\uDEA8'}, new char[]{'\uD835', '\uDEC2'}, new char[]{'\uD835', '\uDFCE'}, Collections.emptyMap()),
    ITALIC("italic", new char[]{'\uD835', '\uDC34'}, new char[]{'\uD835', '\uDC4E'}, new char[]{'\uD835', '\uDEE2'}, new char[]{'\uD835', '\uDEFC'}, new char[]{'\u0030'}, Map.ofEntries(
            Map.entry((int) 'h', new char[]{'\u210E'})
    )),
    BOLD_ITALIC("bold-italic", new char[]{'\uD835', '\uDC68'}, new char[]{'\uD835', '\uDC82'}, new char[]{'\uD835', '\uDF1C'}, new char[]{'\uD835', '\uDF36'}, new char[]{'\uD835', '\uDFCE'}, Collections.emptyMap()),
    DOUBLE_STRUCK("double-struck", new char[]{'\uD835', '\uDD38'}, new char[]{'\uD835', '\uDD52'}, new char[]{'\u0391'}, new char[]{'\u03B1'}, new char[]{'\uD835', '\uDFD8'}, Map.ofEntries(
            Map.entry((int) 'C', new char[]{'\u2102'}),
            Map.entry((int) 'H', new char[]{'\u210D'}),
            Map.entry((int) 'N', new char[]{'\u2115'}),
            Map.entry((int) 'P', new char[]{'\u2119'}),
            Map.entry((int) 'Q', new char[]{'\u211A'}),
            Map.entry((int) 'R', new char[]{'\u211D'}),
            Map.entry((int) 'Z', new char[]{'\u2124'})
    )),
    BOLD_FRAKTUR("bold-fraktur", new char[]{'\uD835', '\uDD6C'}, new char[]{'\uD835', '\uDD86'}, new char[]{'\uD835', '\uDEA8'}, new char[]{'\uD835', '\uDEC2'}, new char[]{'\uD835', '\uDFCE'}, Collections.emptyMap()),
    SCRIPT("script", new char[]{'\uD835', '\uDC9C'}, new char[]{'\uD835', '\uDCB6'}, new char[]{'\u0391'}, new char[]{'\u03B1'}, new char[]{'\u0030'}, Map.ofEntries(
            Map.entry((int) 'B', new char[]{'\u212C'}),
            Map.entry((int) 'E', new char[]{'\u2130'}),
            Map.entry((int) 'F', new char[]{'\u2131'}),
            Map.entry((int) 'H', new char[]{'\u210B'}),
            Map.entry((int) 'J', new char[]{'\u2110'}),
            Map.entry((int) 'L', new char[]{'\u2122'}),
            Map.entry((int) 'M', new char[]{'\u2133'}),
            Map.entry((int) 'R', new char[]{'\u211B'}),
            Map.entry((int) 'e', new char[]{'\u212F'}),
            Map.entry((int) 'g', new char[]{'\u210A'}),
            Map.entry((int) 'o', new char[]{'\u2134'})
    )),
    BOLD_SCRIPT("bold-script", new char[]{'\uD835', '\uDCD0'}, new char[]{'\uD835', '\uDCEA'}, new char[]{'\uD835', '\uDEA8'}, new char[]{'\uD835', '\uDEC2'}, new char[]{'\uD835', '\uDFCE'}, Collections.emptyMap()),
    FRAKTUR("fraktur", new char[]{'\uD835', '\uDD04'}, new char[]{'\uD835', '\uDD1E'}, new char[]{'\u0391'}, new char[]{'\u03B1'}, new char[]{'\u0030'}, Map.ofEntries(
            Map.entry((int) 'C', new char[]{'\u212D'}),
            Map.entry((int) 'H', new char[]{'\u210C'}),
            Map.entry((int) 'I', new char[]{'\u2111'}),
            Map.entry((int) 'R', new char[]{'\u211C'}),
            Map.entry((int) 'Z', new char[]{'\u2128'})
    )),
    SANS_SERIF("sans-serif", new char[]{'\uD835', '\uDDA0'}, new char[]{'\uD835', '\uDDBA'}, new char[]{'\u0391'}, new char[]{'\u03B1'}, new char[]{'\uD835', '\uDFE2'}, Collections.emptyMap()),
    BOLD_SANS_SERIF("bold-sans-serif", new char[]{'\uD835', '\uDDD4'}, new char[]{'\uD835', '\uDDEE'}, new char[]{'\uD835', '\uDF56'}, new char[]{'\uD835', '\uDF70'}, new char[]{'\uD835', '\uDFEC'}, Collections.emptyMap()),
    SANS_SERIF_ITALIC("sans-serif-italic", new char[]{'\uD835', '\uDE08'}, new char[]{'\uD835', '\uDE22'}, new char[]{'\uD835', '\uDEE2'}, new char[]{'\uD835', '\uDEFC'}, new char[]{'\uD835', '\uDFE2'}, Collections.emptyMap()),
    SANS_SERIF_BOLD_ITALIC("sans-serif-bold-italic", new char[]{'\uD835', '\uDE3C'}, new char[]{'\uD835', '\uDE56'}, new char[]{'\uD835', '\uDF90'}, new char[]{'\uD835', '\uDFAA'}, new char[]{'\uD835', '\uDFEC'}, Collections.emptyMap()),
    MONOSPACE("monospace", new char[]{'\uD835', '\uDE70'}, new char[]{'\uD835', '\uDE8A'}, new char[]{'\u0391'}, new char[]{'\u03B1'}, new char[]{'\uD835', '\uDFF6'}, Collections.emptyMap());

    /**
     * Lookup for a variant by its name.
     */
    private static final Map<String, MathVariant> LOOKUP = new HashMap<>();

    /**
     * Name of the variant.
     */
    private final String name;

    /**
     * Start character for capital letters.
     */
    private final char[] capitalLettersStart;

    /**
     * Start character for small letters.
     */
    private final char[] smallLettersStart;

    /**
     * Start character for greek capital letters.
     */
    private final char[] greekCapitalLettersStart;

    /**
     * Start character for greek small letters.
     */
    private final char[] greekSmallLettersStart;

    /**
     * Start character for digits.
     */
    private final char[] digitsStart;

    /**
     * Exception: Code points that cannot be retrieved using addition with an offset.
     * For example the capital Z letter for double-struck cannot be retrieved by adding 25 to
     * the normal Z char value.
     */
    private Map<Integer, char[]> exceptions;

    MathVariant(
            String name,
            char[] capitalLettersStart,
            char[] smallLettersStart,
            char[] greekCapitalLettersStart,
            char[] greekSmallLettersStart,
            char[] digitsStart,
            Map<Integer, char[]> exceptions
    ) {
        this.name = name;
        this.capitalLettersStart = capitalLettersStart;
        this.smallLettersStart = smallLettersStart;
        this.greekCapitalLettersStart = greekCapitalLettersStart;
        this.greekSmallLettersStart = greekSmallLettersStart;
        this.digitsStart = digitsStart;
        this.exceptions = exceptions;
    }

    /**
     * Get the name of the variant.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the start character for capital letters.
     *
     * @return capital letters start
     */
    public char[] getCapitalLettersStart() {
        return capitalLettersStart;
    }

    /**
     * Get the start character for small letters.
     *
     * @return small letters start
     */
    public char[] getSmallLettersStart() {
        return smallLettersStart;
    }

    /**
     * Get the start character for greek capital letters.
     *
     * @return greek capital letters start
     */
    public char[] getGreekCapitalLettersStart() {
        return greekCapitalLettersStart;
    }

    /**
     * Get the start character for greek small letters.
     *
     * @return greek small letters start
     */
    public char[] getGreekSmallLettersStart() {
        return greekSmallLettersStart;
    }

    /**
     * Get the start character for digits.
     *
     * @return digits start
     */
    public char[] getDigitsStart() {
        return digitsStart;
    }

    /**
     * Get the capital letter for the passed offset.
     *
     * @param offset the offset
     * @return capital letter
     */
    public char[] getCapitalLetter(int offset) {
        return getLetter(capitalLettersStart, 'A', offset);
    }

    /**
     * Get the small letter for the passed offset.
     *
     * @param offset the offset
     * @return small letter
     */
    public char[] getSmallLetter(int offset) {
        return getLetter(smallLettersStart, 'a', offset);
    }

    /**
     * Get the greek capital letter for the passed offset.
     *
     * @param offset the offset
     * @return small letter
     */
    public char[] getGreekCapitalLetter(int offset) {
        return getLetter(greekCapitalLettersStart, '\u0391', offset);
    }

    /**
     * Get the greek small letter for the passed offset.
     *
     * @param offset the offset
     * @return small letter
     */
    public char[] getGreekSmallLetter(int offset) {
        return getLetter(greekSmallLettersStart, '\u03B1', offset);
    }

    /**
     * Get the digit letter for the passed offset.
     *
     * @param offset the offset
     * @return small letter
     */
    public char[] getDigitLetter(int offset) {
        return getLetter(digitsStart, '0', offset);
    }

    /**
     * Get the correct letter.
     *
     * @param start      of the letter
     * @param normalChar the normal letter start
     * @param offset     the offset
     * @return the correct letter
     */
    private char[] getLetter(char[] start, char normalChar, int offset) {
        int normalCharCodePoint = (int) normalChar + offset;
        if (exceptions.containsKey(normalCharCodePoint)) {
            return exceptions.get(normalCharCodePoint);
        }

        char[] result = new char[start.length];

        for (int i = 0; i < start.length - 1; i++) {
            result[i] = start[i];
        }

        result[start.length - 1] = (char) (start[start.length - 1] + offset);

        return result;
    }

    /**
     * Get the math variant for the passed name.
     *
     * @param name to get variant for
     * @return optional variant
     */
    public static Optional<MathVariant> forName(String name) {
        if (LOOKUP.isEmpty()) {
            for (MathVariant variant : values()) {
                LOOKUP.put(variant.getName(), variant);
            }
        }

        return Optional.ofNullable(LOOKUP.get(name));
    }

}
