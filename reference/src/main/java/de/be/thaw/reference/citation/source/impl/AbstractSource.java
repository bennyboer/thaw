package de.be.thaw.reference.citation.source.impl;

import de.be.thaw.reference.citation.source.Source;

/**
 * An abstract source.
 */
public abstract class AbstractSource implements Source {

    /**
     * Identifier of the source.
     */
    private final String identifier;

    public AbstractSource(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

}
