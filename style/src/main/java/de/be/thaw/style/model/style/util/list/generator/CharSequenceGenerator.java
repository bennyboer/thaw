package de.be.thaw.style.model.style.util.list.generator;

import de.be.thaw.style.model.style.util.list.ListStyleGenerator;

/**
 * List item style generator using a fixed char sequence.
 */
public class CharSequenceGenerator implements ListStyleGenerator {

    /**
     * Character sequence to use for generating list item symbols.
     */
    private final char[] chars;

    public CharSequenceGenerator(char[] chars) {
        this.chars = chars;
    }

    @Override
    public String generate(int sequence) {
        StringBuilder sb = new StringBuilder();

        do {
            sequence = sequence - 1;

            int rest = sequence % chars.length;
            sequence /= chars.length;

            sb.append(chars[rest]);
        } while (sequence > 0);

        return sb.reverse().toString();
    }

}
