package de.be.thaw.text.tokenizer.token;

import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.util.TextPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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

    /**
     * A class name in case the text emphases contains a custom class name.
     */
    @Nullable
    private final String className;

    public FormattedToken(String value, TextPosition position, Set<TextEmphasis> emphases) {
        this(value, position, emphases, null);
    }

    public FormattedToken(String value, TextPosition position, Set<TextEmphasis> emphases, @Nullable String className) {
        super(value, position);
        this.emphases = emphases;
        this.className = className;
    }

    @Override
    public TokenType getType() {
        return TokenType.FORMATTED;
    }

    /**
     * Get a set of all emphases in the formatted token.
     *
     * @return emphases
     */
    public Set<TextEmphasis> getEmphases() {
        return emphases;
    }

    /**
     * Get a class name in case the emphases contains a custom emphasis.
     *
     * @return class name
     */
    public Optional<String> getClassName() {
        return Optional.ofNullable(className);
    }

    @Override
    public String toString() {
        return String.format("%s > %s", super.toString(), getEmphases().stream()
                .map(TextEmphasis::name)
                .sorted()
                .collect(Collectors.joining(", ")));
    }

}
