package de.be.thaw.text.tokenizer.util;

/**
 * A function functional interface that will rethrow exception.s
 *
 * @param <T> parameter type
 * @param <R> result type
 * @param <E> the exception type
 */
@FunctionalInterface
public interface RethrowingFunction<T, R, E extends Exception> {

    /**
     * Apply the given parameter to the function.
     *
     * @param parameter to apply
     * @return the result R
     * @throws E in case something went wrong
     */
    R apply(T parameter) throws E;

}
