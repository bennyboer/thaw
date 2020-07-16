package de.be.thaw.typeset.knuthplass.util;

/**
 * A rethrowing bi function functional interface.
 *
 * @param <A> the first parameter type
 * @param <B> the second parameter type
 * @param <R> the result type
 * @param <E> the exception that might occur
 */
@FunctionalInterface
public interface RethrowingBiFunction<A, B, R, E extends Exception> {

    /**
     * Apply the function.
     *
     * @param a parameter a
     * @param b parameter b
     * @return the result R
     * @throws E the exception that might happen
     */
    R apply(A a, B b) throws E;

}
