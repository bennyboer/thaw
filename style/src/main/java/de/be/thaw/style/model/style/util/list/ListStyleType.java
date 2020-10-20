package de.be.thaw.style.model.style.util.list;

import de.be.thaw.style.model.style.util.list.generator.CharSequenceGenerator;
import de.be.thaw.style.model.style.util.list.generator.RomanSequenceGenerator;

/**
 * Available style types for list items.
 */
public enum ListStyleType {

    BULLET(n -> "\u2022"),
    MINUS(n -> "-"),
    ASTERISK(n -> "\u2217"),
    CIRCLE(n -> "\u25CB"),

    DECIMAL(String::valueOf),
    LOWER_LATIN(new CharSequenceGenerator("abcdefghijklmnopqrstuvwxyz".toCharArray())),
    UPPER_LATIN(new CharSequenceGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())),
    LOWER_ROMAN(new RomanSequenceGenerator(false)),
    UPPER_ROMAN(new RomanSequenceGenerator(true));

    /**
     * The symbol generator to use.
     */
    private final ListStyleGenerator symbolGenerator;

    ListStyleType(ListStyleGenerator symbolGenerator) {
        this.symbolGenerator = symbolGenerator;
    }

    /**
     * Get the symbol generator to use.
     *
     * @return generator
     */
    public ListStyleGenerator getSymbolGenerator() {
        return symbolGenerator;
    }

}
