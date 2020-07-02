package de.be.thaw.typeset.knuthplass.util;

/**
 * Consumer functional interface that is rethrowing exceptions
 *
 * @param <T> type to consume
 * @param <E> exception type
 */
public interface RethrowingConsumer<T, E extends Throwable> {

    /**
     * Accept the passed item.
     *
     * @param item to accept
     * @throws E exception that could be rethrown
     */
    void accept(T item) throws E;

}
