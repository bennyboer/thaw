package de.be.thaw.reference.citation.source.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.model.parser.impl.SourceModelDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Model of all sources.
 */
@JsonDeserialize(using = SourceModelDeserializer.class)
public class SourceModel {

    /**
     * All sources mapped by their identifiers.
     */
    private final Map<String, Source> sources = new HashMap<>();

    /**
     * Add a source.
     *
     * @param source to add
     */
    public void addSource(Source source) {
        sources.put(source.getIdentifier(), source);
    }

    /**
     * Get a source by its identifier.
     *
     * @param identifier to get source for
     * @return the source
     */
    public Optional<Source> getSource(String identifier) {
        return Optional.ofNullable(sources.get(identifier));
    }

}
