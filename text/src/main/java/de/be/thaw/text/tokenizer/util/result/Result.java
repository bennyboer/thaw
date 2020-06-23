package de.be.thaw.text.tokenizer.util.result;

import org.jetbrains.annotations.Nullable;

/**
 * A result being either an exception or a value.
 *
 * @param <R> the resulting value
 * @param <E> the exception type
 */
public class Result<R, E extends Exception> {

    /**
     * The resulting value.
     */
    @Nullable
    private final R result;

    /**
     * The error.
     */
    @Nullable
    private final E error;

    /**
     * Create successful result.
     *
     * @param result value
     * @param <R>    the result type that happened
     * @param <E>    the happening exception type that could have happened
     * @return the result
     */
    public static <R, E extends Exception> Result<R, E> success(R result) {
        return new Result<>(result, null);
    }

    /**
     * Create error result.
     *
     * @param error that happened
     * @param <R>   the result type that could have happened
     * @param <E>   the happening exception type
     * @return the result
     */
    public static <R, E extends Exception> Result<R, E> error(E error) {
        return new Result<>(null, error);
    }

    private Result(@Nullable R result, @Nullable E error) {
        this.result = result;
        this.error = error;
    }

    /**
     * Whether the result is an error.
     *
     * @return whether an error
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * Get the error that happened.
     *
     * @return error
     */
    public E error() {
        return error;
    }

    /**
     * Get the result value.
     *
     * @return result value
     */
    public R result() {
        return result;
    }

}
