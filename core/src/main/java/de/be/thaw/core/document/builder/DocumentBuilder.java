package de.be.thaw.core.document.builder;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;

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
     * @throws DocumentBuildException in case the document could not be built properly
     */
    Document build(S source) throws DocumentBuildException;

}
