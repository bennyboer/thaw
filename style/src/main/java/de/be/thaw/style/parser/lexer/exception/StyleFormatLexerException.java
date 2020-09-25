package de.be.thaw.style.parser.lexer.exception;

import de.be.thaw.util.parser.location.TextFilePosition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Exception thrown when the style format lexing process encountered a problem.
 */
public class StyleFormatLexerException extends Exception {

    /**
     * Position of where the error happened (if known).
     */
    @Nullable
    private final TextFilePosition position;

    public StyleFormatLexerException(String message) {
        this(message, null);
    }

    /**
     * Get the position where the exception happened (if known).
     *
     * @return position
     */
    public Optional<TextFilePosition> getPosition() {
        return Optional.ofNullable(position);
    }

    /**
     * Create exception that happened at a known location in the read file.
     *
     * @param message  to describe the problem further
     * @param position of where it happened
     */
    public StyleFormatLexerException(String message, @Nullable TextFilePosition position) {
        this(message, null, position);
    }

    public StyleFormatLexerException(String message, @Nullable Throwable cause, @Nullable TextFilePosition position) {
        super(String.format(
                "Encountered a problem during lexing the provided style format%s with error message '%s'%s",
                position != null ? String.format(" in line %s at position %d", position.getLine(), position.getPosition()) : "",
                message,
                cause != null ? String.format(" and causing exception: '%s'", cause.getMessage()) : ""
        ));

        this.position = position;
    }

}
