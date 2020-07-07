package de.be.thaw.core.document.builder;

import de.be.thaw.core.document.Document;

/**
 * Builder building a document from the provided sources.
 *
 * @param <S> the source type
 */
public interface DocumentBuilder<S> {

    /**
     * Build a document from the provided source.
     *
     * @param source to build document from
     * @return the built document
     */
    Document build(S source);

}
