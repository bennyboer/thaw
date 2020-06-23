package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.model.element.emphasis.TextEmphasis;
import de.be.thaw.text.util.TextRange;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Token of formatted text.
 */
public class FormattedToken extends DefaultToken {

    /**
     * The formats set.
     */
    private final Set<TextEmphasis> emphases;

    public FormattedToken(String value, TextRange range, Set<TextEmphasis> emphases) {
        super(value, range);
        this.emphases = emphases;
    }

    @Override
    public TokenType getType() {
        return TokenType.FORMATTED;
    }

    public Set<TextEmphasis> getEmphases() {
        return emphases;
    }

    @Override
    public String toString() {
        return String.format("%s > %s", super.toString(), getEmphases().stream()
                .map(TextEmphasis::name)
                .sorted()
                .collect(Collectors.joining(", ")));
    }

}
