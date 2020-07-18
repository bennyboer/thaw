package de.be.thaw.reference.citation.styles.apa;

import de.be.thaw.reference.citation.CitationStyle;
import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.styles.SourceHandler;
import de.be.thaw.reference.citation.styles.apa.handler.ArticleHandler;
import de.be.thaw.reference.citation.styles.apa.handler.BookHandler;
import de.be.thaw.reference.citation.styles.apa.handler.EBookHandler;
import de.be.thaw.reference.citation.styles.apa.handler.OnlineBookHandler;
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;
import de.be.thaw.reference.citation.styles.exception.UnsupportedSourceTypeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The APA citation style.
 */
public class APA implements CitationStyle {

    /**
     * Mapping of source types to the handlers that are able
     * to deal with them.
     */
    private static final Map<SourceType, SourceHandler> SOURCE_HANDLERS = new EnumMap<>(SourceType.class);

    static {
        initSourceHandler(new BookHandler());
        initSourceHandler(new EBookHandler());
        initSourceHandler(new OnlineBookHandler());
        initSourceHandler(new ArticleHandler());
    }

    /**
     * Initialize the passed source handler.
     *
     * @param handler to initialize
     */
    private static void initSourceHandler(SourceHandler handler) {
        for (SourceType type : handler.supports()) {
            SOURCE_HANDLERS.put(type, handler);
        }
    }

    /**
     * Get a source handler for the passed type.
     *
     * @param type to get source handler for
     * @return source handler
     */
    private static Optional<SourceHandler> getSourceHandler(SourceType type) {
        return Optional.ofNullable(SOURCE_HANDLERS.get(type));
    }

    @Override
    public String getName() {
        return "APA";
    }

    @Override
    public String buildReferenceListEntry(Source source) throws UnsupportedSourceTypeException, ReferenceBuildException {
        return getSourceHandler(source.getType()).orElseThrow(() -> new UnsupportedSourceTypeException(String.format(
                "Could not build reference list entry because the source type '%s' is not supported by the citation style",
                source.getType().name()
        ))).buildReferenceListEntry(source);
    }

    @Override
    public String buildInTextCitation(List<Source> sources, List<String> positions) throws UnsupportedSourceTypeException, ReferenceBuildException {
        List<String> parts = new ArrayList<>(sources.size());

        for (int i = 0; i < sources.size(); i++) {
            Source source = sources.get(i);
            String position = positions.get(i);

            parts.add(getSourceHandler(source.getType()).orElseThrow(() -> new UnsupportedSourceTypeException(String.format(
                    "Could not build in-text-citation because the source type '%s' is not supported by the citation style",
                    source.getType().name()
            ))).buildInTextCitation(source, position));
        }

        // Sort sources alphabetically
        Collections.sort(parts);

        // Separate the individual sources by semicolons
        return String.join("; ", parts);
    }

}
