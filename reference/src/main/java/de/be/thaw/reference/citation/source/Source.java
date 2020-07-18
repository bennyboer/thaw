package de.be.thaw.reference.citation.source;

/**
 * Representation of a source (books, websites, articles, journals, ...).
 */
public interface Source {

    /**
     * Get the type of the source.
     *
     * @return type
     */
    SourceType getType();

}
